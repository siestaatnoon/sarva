package com.cccdlabs.sarva.presentation.ui.fragments.base;

import android.app.Fragment;
import android.widget.Toast;

import com.cccdlabs.sarva.presentation.di.HasComponent;

/**
 * Base class for fragments to allow simple Dagger component retrieval for dependency
 * injection and for showing messages.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class BaseFragment extends Fragment {
    /**
     * Shows a {@link android.widget.Toast} message.
     *
     * @param message A string representing a message to be shown
     */
    protected void showToastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Gets a component for dependency injection by its type.
     *
     * @param componentType Class of component
     * @return              The Dagger component
     */
    @SuppressWarnings("unchecked")
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }
}