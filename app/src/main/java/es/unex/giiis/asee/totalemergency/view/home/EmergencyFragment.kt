package es.unex.giiis.asee.totalemergency.view.home


import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK

import android.content.Intent

import android.net.Uri
import android.os.Bundle

import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts

import androidx.fragment.app.activityViewModels

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

import es.unex.giiis.asee.totalemergency.databinding.FragmentEmergencyBinding



/**
 * A simple [Fragment] subclass.
 * Use the [EmergencyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EmergencyFragment : Fragment() {

    private val homeViewModel: HomeViewModel by activityViewModels()

    private var _binding: FragmentEmergencyBinding? = null
    private val binding get() = _binding!!

    private lateinit var videoUri : Uri


    private val viewModel : EmergencyViewModel by viewModels { EmergencyViewModel.Factory }

    private val responseCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == RESULT_OK){

                //Pass the uri to view model, view model will pass it to Repository.
                //Manage files
                viewModel.retrieveUriData(result.data?.data!!, requireContext())

            }else if(result.resultCode == RESULT_CANCELED){
                Log.i("VIDEO_RECORD_TAG", "Video recording is cancelled")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.obtainPermission(requireContext(), activity as HomeActivity)
        viewModel.obtainStoragePermission(requireContext(), activity as HomeActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentEmergencyBinding.inflate(inflater, container, false)

        homeViewModel.user.observe(viewLifecycleOwner) { us ->
            viewModel.user = us
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()

    }

    private fun observeCameraResponse(){
        viewModel.cameraResponse.observe(viewLifecycleOwner, Observer { response ->
            Log.d("OBSERVER", "The response is: [${response}]")
        })
    }

    fun getPath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = context?.contentResolver?.query(uri, projection, null, null, null)
        cursor?.use {
            Log.i("Cursor", "Trying to fetch the data")
            if(it.moveToFirst()){
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    fun setUpListeners() {

        with(binding){
            //emergencyButton.onKeyLongPress(3)
            emergency.setOnClickListener {

                viewModel.cameraPermission.observe(viewLifecycleOwner, Observer { response ->
                    if(response){
                        Log.i("Acceso", "Existe acceso a la camara")


                        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                        responseCamera.launch(intent)
                        observeCameraResponse()
                    }else{
                        Log.i("Acceso", "NO Existe acceso a la camara")
                    }
                })
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EmergencyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EmergencyFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}