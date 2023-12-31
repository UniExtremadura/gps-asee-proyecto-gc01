package es.unex.giiis.asee.totalemergency.view.home

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import es.unex.giiis.asee.totalemergency.view.home.HomeViewModel
import es.unex.giiis.asee.totalemergency.view.home.ProfileUpdaterViewModel
import es.unex.giiis.asee.totalemergency.data.database.TotalEmergencyDatabase
import es.unex.giiis.asee.totalemergency.data.model.User
import es.unex.giiis.asee.totalemergency.databinding.FragmentProfileUpdaterBinding
import es.unex.giiis.asee.totalemergency.view.LoginActivity
import kotlinx.coroutines.launch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileUpdaterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileUpdaterFragment : Fragment() {

    private val homeViewModel: HomeViewModel by activityViewModels()

    private val viewModel : ProfileUpdaterViewModel by viewModels { ProfileUpdaterViewModel.Factory }

    private var _binding : FragmentProfileUpdaterBinding? = null
    private val binding get() = _binding!!

    private lateinit var db : TotalEmergencyDatabase

    private var popping : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileUpdaterBinding.inflate(inflater, container, false)

        homeViewModel.user.observe(viewLifecycleOwner) { us ->
            viewModel.user = us
            viewModel.updateUser()
        }

        db = TotalEmergencyDatabase.getInstance((activity as HomeActivity).applicationContext)!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            popup.visibility = View.GONE

            viewModel.userView.observe(viewLifecycleOwner){ it ->
                UsernameTextShow.text = viewModel.user?.userName
                NameTextShow.text = viewModel.user?.name
                LastnameTextShow.text = viewModel.user?.lastName
                EmailTextShow.text = viewModel.user?.email

                editUsername.setText(viewModel.user?.userName)
                editName.setText (viewModel.user?.name)
                editLastname.setText (viewModel.user?.lastName)
                editEmail.setText (viewModel.user?.email)
            }
        }

        setUpListener()
    }


    private fun setUpListener(){

        with(binding){

            borrar.setOnClickListener {
                popping = !popping
                popup.visibility = if(popping) View.VISIBLE else View.GONE
                Log.i("PRE-ELIMINAR", "El valor es: ${popping}")
            }

            modificar.setOnClickListener {

                val tempUser = User(cod=viewModel.user?.cod,userName=editUsername.text.toString(),
                    name=editName.text.toString(), lastName=editLastname.text.toString(),
                    email=editEmail.text.toString(),userPassword=viewModel.user?.userPassword!!,
                    addres=viewModel.user?.addres!!,city=viewModel.user?.city!!,
                    country=viewModel.user?.country!!,telephone=viewModel.user?.telephone!!)

                editUsername.text.clear()
                editName.text.clear()
                editLastname.text.clear()
                editEmail.text.clear()
123
                viewModel.modifyUser(tempUser, requireContext())
                viewModel.userView.observe(viewLifecycleOwner){ it->
                    homeViewModel.obtenerUser(tempUser.cod!!)
                }

                viewModel.userView.observe(viewLifecycleOwner){ it ->
                    UsernameTextShow.text = viewModel.user?.userName
                    NameTextShow.text = viewModel.user?.name
                    LastnameTextShow.text = viewModel.user?.lastName
                    EmailTextShow.text = viewModel.user?.email
                }
            }


            buttonDelete.setOnClickListener {
                Log.i("ELIMINAR", "Borrando usuario y su informacion")

                viewModel.deleteUser(requireContext())
                lifecycleScope.launch{
                    val cod = viewModel.user?.cod!!
                    db.userDao().deleteByCod(cod)

                    db.videoDAO().deleteFromUserId(cod) //TODO: Clear videos from memory

                    db.contactDAO().deleteFromUserId(cod)
                    startActivity(Intent(context, LoginActivity::class.java))
                }

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
         * @return A new instance of fragment ProfileUpdaterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileUpdaterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}