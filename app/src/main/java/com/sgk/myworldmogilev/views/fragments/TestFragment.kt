package com.sgk.myworldmogilev.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.sgk.myworldmogilev.adapters.TestAdapter
import com.sgk.myworldmogilev.databinding.FragmentTestBinding
import com.sgk.myworldmogilev.helper.ListPositioner
import com.sgk.myworldmogilev.models.TestList

class TestFragment : Fragment(), ListPositioner {

    private lateinit var database: DatabaseReference
    private lateinit var binding: FragmentTestBinding
    private var list = mutableListOf<TestList>()
    override val recyclerScrollKey = "recycler_view_position"

    override fun loadListPosition() {
        val scrollPosition =
            PreferenceManager.getDefaultSharedPreferences(requireActivity())
                .getInt(recyclerScrollKey, 0)
        binding.recyclerView.scrollToPosition(scrollPosition)
    }

    override fun saveListPosition() {
        val position =
            (binding.recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        PreferenceManager.getDefaultSharedPreferences(requireActivity())
            .edit()
            .putInt(recyclerScrollKey, position)
            .apply()
    }

    companion object {
        @JvmStatic
        fun newInstance() = TestFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        list.add(TestList())
        binding = FragmentTestBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database.getReference("testName")

        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    val value = data.getValue<TestList>()
                    if (value != null)
                        list.add(value)
                }

                binding.recyclerView.adapter = TestAdapter(list, activity)

                binding.progressTest.animate()
                    .alpha(0F)
                    .withEndAction {
                        binding.progressTest.visibility = View.GONE
                    }
                    .setDuration(500)
                    .start()

            }

            override fun onCancelled(error: DatabaseError) {}
        })

        binding.recyclerView.setHasFixedSize(true)

        loadListPosition()
    }

    override fun onPause() {
        saveListPosition()
        super.onPause()
    }
}
