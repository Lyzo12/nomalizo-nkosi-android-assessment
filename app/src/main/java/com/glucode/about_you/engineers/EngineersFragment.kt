package com.glucode.about_you.engineers

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.glucode.about_you.R
import com.glucode.about_you.databinding.FragmentEngineersBinding
import com.glucode.about_you.engineers.models.Engineer
import com.glucode.about_you.mockdata.MockData

class EngineersFragment : Fragment() {
    private lateinit var binding: FragmentEngineersBinding
    private lateinit var adapter: EngineersRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEngineersBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        setUpEngineersList(MockData.engineers)

        parentFragmentManager.setFragmentResultListener("profileImageUpdated", this) { _, result ->
            val engineerName = result.getString("engineerName")
            val newImageUri = result.getString("newImageUri")
            //if there is nothing implement the changes to update the name and image
            if (engineerName != null && newImageUri != null) {
                updateEngineerImageList(engineerName, newImageUri)
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_engineers, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_years -> {
                setUpEngineersList(MockData.engineers.sortedBy { it.quickStats.years })
                return true
            }
            R.id.action_coffees -> {
                setUpEngineersList(MockData.engineers.sortedBy { it.quickStats.coffees })
                return true
            }
            R.id.action_bugs -> {
                setUpEngineersList(MockData.engineers.sortedBy { it.quickStats.bugs })
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


     fun setUpEngineersList(engineers: List<Engineer>) {
        binding.list.adapter = EngineersRecyclerViewAdapter(engineers) {
            goToAbout(it)
        }
        val dividerItemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(dividerItemDecoration)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateEngineerImageList(engineerName: String, newImageUri: String) {
        // Update the image of the corresponding engineer in the list
        MockData.engineers.find { it.name == engineerName }?.let {
            it.defaultImageName = newImageUri
            adapter.notifyDataSetChanged()  // Refresh the list
        }
    }

    private fun goToAbout(engineer: Engineer) {
        val bundle = Bundle().apply {
            putString("name", engineer.name)
        }
        findNavController().navigate(R.id.action_engineersFragment_to_aboutFragment, bundle)
    }
}