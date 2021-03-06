package com.makenshi.snapshots

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.makenshi.snapshots.databinding.FragmentHomeBinding
import com.makenshi.snapshots.databinding.ItemSnapshotBinding

class HomeFragment : Fragment(), HomeAux {

    val TAG = HomeFragment::class.java.name
    private lateinit var mBinding: FragmentHomeBinding
    private lateinit var mFirebaseAdapter: FirebaseRecyclerAdapter<Snapshot, SnapshotHolder>
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val query = FirebaseDatabase.getInstance().reference.child("snapshots")
        val options =
        FirebaseRecyclerOptions.Builder<Snapshot>().setQuery(query, SnapshotParser {
            val snapshot = it.getValue(Snapshot::class.java)
            snapshot!!.id = it.key!!
            snapshot
        }).build()

        mFirebaseAdapter = object : FirebaseRecyclerAdapter<Snapshot, SnapshotHolder>(options) {
            private lateinit var mContext: Context
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapshotHolder {
                mContext = parent.context

                val view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_snapshot, parent, false)
                return SnapshotHolder(view)
            }

            override fun onBindViewHolder(holder: SnapshotHolder, position: Int, model: Snapshot) {
                val snapshot = getItem(position)
                with(holder){
                    setListener(snapshot)

                    binding.tvTitle.text = snapshot.title
                    binding.cbLike.text = snapshot.likeList.keys.size.toString()
                    FirebaseAuth.getInstance().currentUser?.let {
                        binding.cbLike.isChecked = snapshot.likeList
                            .containsKey(it.uid)
                    }

                    Glide.with(mContext)
                        .load(snapshot.photoUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(binding.imgPhoto)
                }
            }

            override fun onDataChanged() {
                super.onDataChanged()
                mBinding.progressBar.visibility = View.GONE
            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                Toast.makeText(mContext, error.message, Toast.LENGTH_SHORT).show()
            }
        }

        mLayoutManager = LinearLayoutManager(context)

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mLayoutManager
            adapter = mFirebaseAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        mFirebaseAdapter.startListening()
    }

    override fun onStop() {
        mFirebaseAdapter.stopListening()
        super.onStop()
    }

    override fun goToTop() {
        mBinding.recyclerView.scrollToPosition(0)
    }

    private fun deleteSnapshot(snapshot: Snapshot) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child(SnapshotsApplication.PATH_SNAPSHOTS)
        databaseReference.child(snapshot.id).removeValue()
    }

    private fun setLike(snapshot: Snapshot, checked: Boolean) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child(SnapshotsApplication.PATH_SNAPSHOTS)
        if (checked) {
            databaseReference.child(snapshot.id).child(SnapshotsApplication.PROPERTY_LIKE_LIST)
                .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(checked)
        } else {
            databaseReference.child(snapshot.id).child(SnapshotsApplication.PROPERTY_LIKE_LIST)
                .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(null)
        }
    }

    inner class SnapshotHolder(view : View) : RecyclerView.ViewHolder(view) {
        val binding = ItemSnapshotBinding.bind(view)

        fun setListener(snapshot: Snapshot) {
            binding.btnDelete.setOnClickListener { deleteSnapshot(snapshot) }

            binding.cbLike.setOnCheckedChangeListener { buttonView, isChecked ->
                setLike(snapshot, isChecked)
            }
        }

    }
}