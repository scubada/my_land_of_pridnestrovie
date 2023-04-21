package com.sgk.myworldmogilev.views.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sgk.myworldmogilev.R
import com.sgk.myworldmogilev.databinding.FragmentInfoBinding
import com.sgk.myworldmogilev.views.activities.ContactsActivity
import com.sgk.myworldmogilev.views.activities.SendLetterActivity

class InfoFragment : Fragment() {

    private lateinit var binding: FragmentInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoBinding.inflate(inflater)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = InfoFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.site.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_site))))
        }

        binding.contact.setOnClickListener {
            startActivity(Intent(requireContext(), ContactsActivity::class.java))
            requireActivity().overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
        }

        binding.writeLetter.setOnClickListener {
            startActivity(Intent(requireContext(), SendLetterActivity::class.java))
            requireActivity().overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
        }
    }
}