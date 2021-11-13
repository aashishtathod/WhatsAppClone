package com.example.whatsappclone.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.whatsappclone.fragments.CallsFragment;
import com.example.whatsappclone.fragments.ChatsFragment;
import com.example.whatsappclone.fragments.StatusFragment;
        // adapter to switch fragments of call , status and chats
public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1: return new StatusFragment();
            case 2: return new CallsFragment();
            default: return  new ChatsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
