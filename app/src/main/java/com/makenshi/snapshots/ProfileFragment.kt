package com.makenshi.snapshots

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.makenshi.snapshots.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    val TAG = ProfileFragment::class.java.name

    private lateinit var mBinding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentProfileBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.tvName.text = FirebaseAuth.getInstance().currentUser?.displayName
        mBinding.tvEmail.text = FirebaseAuth.getInstance().currentUser?.email

        mBinding.btnLogout.setOnClickListener {
            singOut()
        }
    }

    private fun singOut() {
        context?.let { mContext ->
            AuthUI.getInstance().signOut(mContext)
                .addOnCompleteListener {
                    Toast.makeText(mContext, "Hasta pronto...", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

}