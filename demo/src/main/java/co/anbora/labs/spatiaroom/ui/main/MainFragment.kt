package co.anbora.labs.spatiaroom.ui.main

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import co.anbora.labs.spatiaroom.R
import co.anbora.labs.spatiaroom.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var appDatabase: AppDatabase

    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDatabase = AppDatabase.getInstance(requireContext())

        val spatia_version = view?.findViewById<TextView>(R.id.spatia_version)

        uiScope.launch(Dispatchers.IO) {

            val spatiaVersion = appDatabase.getAmenity().getSpatiaVersion()
            val getAmenity = appDatabase.getAmenity().getAmenityById(1);
            // Log the fetched amenity
            Log.d("AmenityLog", "Fetched amenity: $getAmenity")
            withContext(Dispatchers.Main) {
                spatia_version?.text = spatiaVersion
            }

        }
    }

    override fun onDestroy() {
    job.cancel()
    super.onDestroy()
    }
}
