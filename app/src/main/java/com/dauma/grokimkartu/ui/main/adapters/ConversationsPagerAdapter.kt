package com.dauma.grokimkartu.ui.main.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dauma.grokimkartu.ui.main.PrivateConversationsFragment
import com.dauma.grokimkartu.ui.main.ThomannConversationsFragment

class ConversationsPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {
    // TODO: read fragmentStatePagerAdapter vs FragmentPagerAdapter
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return PrivateConversationsFragment()
            1 -> return ThomannConversationsFragment()
        }
        return PrivateConversationsFragment()
    }
}