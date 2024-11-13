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

        val pt_address = view?.findViewById<TextView>(R.id.address)

        uiScope.launch(Dispatchers.IO) {

            val spatiaVersion = appDatabase.addresses().getSpatiaVersion()
            val address = appDatabase.addresses().getAddressesByCityAndStreet("Lisboa", "Rua do Açúcar")
            val stDistanceTest = appDatabase.addresses().stDistanceTest()
            val makePolygonTest = appDatabase.addresses().makePolygonTest()
            val getNearbyAddresses = appDatabase.addresses().getNearbyAddresses()
            // Log the fetched address and spatialite version
            Log.d("AddressLog", "Fetched address: $address")
            Log.d("ST_DistanceTest", "stDistanceTest: $stDistanceTest")
            Log.d("MakePolygonTest", "MakePolygonTest: $makePolygonTest")
            Log.d("SpatiaLiteLog", "SpatiaLite Version: $spatiaVersion")
            Log.d("NearbyAddresses", "NearbyAddresses: $getNearbyAddresses")
            withContext(Dispatchers.Main) {
                // Manually construct the JSON string
                val addressJson = """
                                    {
                                        "id": ${address.id},
                                        "country": ${address.country?.let { "\"$it\"" } ?: "null"},
                                        "city": ${address.city?.let { "\"$it\"" } ?: "null"},
                                        "postcode": ${address.postcode?.let { "\"$it\"" } ?: "null"},
                                        "wheelchair": ${address.wheelchair?.let { "\"$it\"" } ?: "null"},
                                        "street": ${address.street?.let { "\"$it\"" } ?: "null"},
                                        "housename": ${address.housename?.let { "\"$it\"" } ?: "null"},
                                        "housenumber": ${address.housenumber?.let { "\"$it\"" } ?: "null"},
                                        "geometry": {
                                            "x": ${address.geometry?.x ?: "null"},
                                            "y": ${address.geometry?.y ?: "null"},
                                            "srid": ${address.geometry?.srid ?: "null"}
                                        }
                                    }
                                """.trimIndent()
                pt_address?.text = addressJson
            }

        }
    }

    override fun onDestroy() {
    job.cancel()
    super.onDestroy()
    }
}
