package com.busto.countryfetcher.extensions

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding


/**
 * By using by viewBinding, you can directly access the binding object as a property without
 * the need for null-checking or the _binding property. This ensures that the binding
 * is non-null when accessed.
 *
 * When using by viewBinding, the binding is automatically cleared when the Fragment's view is
 * destroyed. This is achieved through the use of LazyThreadSafetyMode.NONE, which means the
 * binding is not stored as a val but as a lazy property. The lazy property itself is implemented
 * in a way that allows it to be safely cleared when it's no longer needed.
 */
inline fun <T : ViewBinding> Fragment.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T
) = lazy(LazyThreadSafetyMode.NONE) {
    bindingInflater.invoke(layoutInflater)
}