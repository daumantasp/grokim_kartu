package com.dauma.grokimkartu.ui.main

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentConversationBinding
import com.dauma.grokimkartu.general.DummyCell
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.conversations.entities.ConversationPage
import com.dauma.grokimkartu.ui.main.adapters.ConversationAdapter
import com.dauma.grokimkartu.viewmodels.main.ConversationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
    ): View {
        _binding = FragmentConversationBinding.inflate(inflater, container, false)
        binding.model = conversationViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnClickers()
        setupObservers()
        isViewSetup = false

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

        activity?.window?.decorView?.let {
            utils.keyboardUtils.registerListener("ConversationFragment", it) { isOpened, keyboardHeight ->
                if (isOpened) {
                    val data = (binding.conversationsRecyclerView.adapter as ConversationAdapter).data
                    binding.conversationsRecyclerView.scrollToPosition(data.count() - 1)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        utils.keyboardUtils.unregisterListener("ConversationFragment")
    }

    private fun setupOnClickers() {
        binding.conversationsHeaderViewElement.setOnBackClick {
            conversationViewModel.back()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            conversationViewModel.back()
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
        binding.conversationsRecyclerView.setOnTouchListener { view, motionEvent ->
            if (binding.postMessageTextInputEditText.isFocused) {
                utils.keyboardUtils.hideKeyboard(requireView())
                binding.postMessageTextInputEditText.clearFocus()
            }
            false
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    conversationViewModel.uiState.collect {
                        binding.conversationsHeaderViewElement.setTitle(it.title)
                        val data = getAllConversationFromPagesAndReverse(it.conversationPages)
                        if (!isViewSetup) {
                            setupConversationRecyclerView()
                        }
                        reloadRecyclerViewWithNewData(data)
                        binding.conversationsRecyclerView.scrollToPosition(data.count() - 1)
                        if (it.close)
                            findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun onPostMessageClicked() {
        val messageText = binding.postMessageTextInputEditText.editableText.toString()
        conversationViewModel.postMessageClicked(messageText)
        binding.postMessageTextInputEditText.editableText.clear()
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

    private fun setupConversationRecyclerView() {
        binding.conversationsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.conversationsRecyclerView.adapter = ConversationAdapter(
            context = requireContext(),
            utils = utils,
            loadNextPage = { this.conversationViewModel.loadNextConversationPage() }
        )
        isViewSetup = true
    }

    private fun reloadRecyclerViewWithNewData(newData: List<Any>) {
        val adapter = binding.conversationsRecyclerView.adapter
        if (adapter is ConversationAdapter) {
            adapter.data = newData
        }
    }
}