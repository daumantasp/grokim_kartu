package com.dauma.grokimkartu.ui.main

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentConversationBinding
import com.dauma.grokimkartu.general.DummyCell
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.conversations.entities.ConversationPage
import com.dauma.grokimkartu.repositories.conversations.entities.Message
import com.dauma.grokimkartu.ui.main.adapters.*
import com.dauma.grokimkartu.viewmodels.main.ConversationViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ConversationFragment : Fragment() {
    private val conversationViewModel by viewModels<ConversationViewModel>()
    private var isViewSetup: Boolean = false
    @Inject lateinit var utils: Utils

    private var _binding: FragmentConversationBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConversationBinding.inflate(inflater, container, false)
        binding.model = conversationViewModel
        val view = binding.root
        setupObservers()
        isViewSetup = false

        binding.conversationsHeaderViewElement.setOnBackClick {
            conversationViewModel.backClicked()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            conversationViewModel.backClicked()
        }

        binding.postMessageImageButton.setOnClickListener {
            onPostMessageClicked()
        }
        binding.postMessageTextInputEditText.setOnEditorActionListener { textView, actionId, keyEvent ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onPostMessageClicked()
                handled = true
            }
            handled
        }

        val typedValue = TypedValue()
        context?.theme?.resolveAttribute(R.attr.post_message_icon_active_color, typedValue, true)
        val postMessageIconActiveColor = typedValue.data
        context?.theme?.resolveAttribute(R.attr.post_message_icon_inactive_color, typedValue, true)
        val postMessageIconInactiveColor = typedValue.data
        binding.postMessageTextInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val isPostMessageInputNotBlank = p0.toString().isNotBlank()
                val color = if (isPostMessageInputNotBlank) postMessageIconActiveColor else postMessageIconInactiveColor
                binding.postMessageImageButton.backgroundTintList = ColorStateList.valueOf(color)
            }
        })

        binding.conversationsRecyclerView.setOnTouchListener { view, motionEvent ->
            if (binding.postMessageTextInputEditText.isFocused) {
                utils.keyboardUtils.hideKeyboard(requireView())
                binding.postMessageTextInputEditText.clearFocus()
            }
            false
        }

        activity?.window?.decorView?.let {
            utils.keyboardUtils.registerListener("ConversationFragment", it) { isOpened, keyboardHeight ->
                if (isOpened) {
                    val data = (binding.conversationsRecyclerView.adapter as ConversationAdapter).conversation
                    binding.conversationsRecyclerView.scrollToPosition(data.count() - 1)
                }
            }
        }

        conversationViewModel.viewIsReady()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        utils.keyboardUtils.unregisterListener("ConversationFragment")
        conversationViewModel.viewIsDiscarded()
    }

    private fun setupObservers() {
        conversationViewModel.navigation.observe(viewLifecycleOwner, EventObserver {
            handleNavigation(it)
        })
        conversationViewModel.id.observe(viewLifecycleOwner, EventObserver {
            it?.let {
                binding.conversationsHeaderViewElement.setTitle(it)
            }
        })
        conversationViewModel.newConversationPages.observe(viewLifecycleOwner, { conversationPages ->
            val data = getAllConversationFromPagesAndReverse(conversationPages)
            if (isViewSetup == false) {
                setupConversationRecyclerView(data)
            } else {
                reloadRecyclerViewWithNewData(data)
            }
            binding.conversationsRecyclerView.scrollToPosition(data.count() - 1)
        })
        conversationViewModel.nextConversationPage.observe(viewLifecycleOwner, { conversationPages ->
            val data = getAllConversationFromPagesAndReverse(conversationPages)
            reloadRecyclerViewWithNewData(data)
            val position = (conversationPages.lastOrNull()?.messages?.count() ?: 0) + 5
            Log.d("ConversationFragment", "scrolling to ${position}")
            binding.conversationsRecyclerView.scrollToPosition(position)
        })
        conversationViewModel.messagePosted.observe(viewLifecycleOwner, EventObserver {
            binding.conversationsRecyclerView.adapter?.notifyDataSetChanged()
            binding.postMessageTextInputEditText.editableText.clear()
        })
    }

    private fun onPostMessageClicked() {
        val messageText = binding.postMessageTextInputEditText.editableText.toString()
        conversationViewModel.postMessageClicked(messageText)
    }

    private fun getAllConversationFromPagesAndReverse(pages: List<ConversationPage>) : List<Any> {
        val data: MutableList<Any> = mutableListOf()
        for (page in pages) {
            page.messages?.let {
                data.addAll(it)
            }
        }
        if (pages.lastOrNull()?.isLast == false) {
            data.add(DummyCell())
        }
        val reversedData = data.reversed()
        return reversedData
    }

    private fun setupConversationRecyclerView(conversation: List<Any>) {
        binding.conversationsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.conversationsRecyclerView.adapter = ConversationAdapter(
            context = requireContext(),
            conversation = conversation.toMutableList(),
            utils = utils,
            loadNextPage = { this.conversationViewModel.loadNextConversationPage() }
        )
        isViewSetup = true
    }

    private fun reloadRecyclerViewWithNewData(newData: List<Any>) {
        val adapter = binding.conversationsRecyclerView.adapter
        if (adapter is ConversationAdapter) {
            val previousData = adapter.conversation

            val changedItems: MutableList<Int> = mutableListOf()
            val insertedItems: MutableList<Int> = mutableListOf()
            val removedItems: MutableList<Int> = mutableListOf()

            if (previousData.count() <= newData.count()) {
                for (i in 0 until previousData.count()) {
                    val previousItem = previousData[i]
                    val newItem = newData[i]
                    if (previousItem is Message && newItem is Message) {
                        if (previousItem.id != newItem.id) {
                            changedItems.add(i)
                        }
                    }
                    else if (previousItem is DummyCell && newItem is DummyCell) {
                        // DO NOTHING
                    }
                    else {
                        changedItems.add(i)
                    }
                }
                for (i in previousData.count() until newData.count()) {
                    insertedItems.add(i)
                }
            } else {
                for (i in 0 until newData.count()) {
                    val previousItem = previousData[i]
                    val newItem = newData[i]
                    if (previousItem is Message && newItem is Message) {
                        if (previousItem.id != newItem.id) {
                            changedItems.add(i)
                        }
                    }
                    else if (previousItem is DummyCell && newItem is DummyCell) {
                        // DO NOTHING
                    }
                    else {
                        changedItems.add(i)
                    }
                }
                for (i in newData.count() until previousData.count()) {
                    removedItems.add(i)
                }
            }

            val sortedChangedItems = changedItems.sorted()
            val sortedInsertedItems = insertedItems.sorted()
            val sortedRemovedItems = removedItems.sorted()

            val sortedChangedRanges = utils.otherUtils.getRanges(sortedChangedItems)
            val sortedInsertedRanges = utils.otherUtils.getRanges(sortedInsertedItems)
            val sortedRemovedRanges = utils.otherUtils.getRanges(sortedRemovedItems)

            adapter.conversation = newData.toMutableList()
            for (range in sortedRemovedRanges.reversed()) {
                adapter.notifyItemRangeRemoved(range[0], range[1])
            }
            for (range in sortedInsertedRanges) {
                adapter.notifyItemRangeInserted(range[0], range[1])
            }
            for (range in sortedChangedRanges) {
                adapter.notifyItemRangeChanged(range[0], range[1])
            }
        }
    }

    private fun handleNavigation(navigationCommand: NavigationCommand) {
        when (navigationCommand) {
            is NavigationCommand.ToDirection -> findNavController().navigate(navigationCommand.directions)
            is NavigationCommand.Back -> findNavController().popBackStack()
            is NavigationCommand.CloseApp -> activity?.finish()
        }
    }
}