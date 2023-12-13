package es.unex.giiis.asee.totalmergency.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import es.unex.giiis.asee.totalmergency.data.database.TotalEmergencyDatabase
import es.unex.giiis.asee.totalmergency.data.model.Contact
import es.unex.giiis.asee.totalmergency.databinding.ContactItemListBinding
import es.unex.giiis.asee.totalmergency.databinding.RecordItemListBinding
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ContactsAdapter(
    private val contacts: List<Contact>,
    private val onClick: (contact: Contact) -> Unit,
    private val onLongClick: (uri: Contact) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ShowViewHolder>() {

    class ShowViewHolder(
        private val binding: ContactItemListBinding,
        private val onClick: (contact: Contact) -> Unit,
        private val onLongClick: (uri: Contact) -> Unit
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(contact: Contact, totalItems: Int){
            with(binding){
                //uriPath.text = video.uri
                labelNumber.text = contact.contactName
                telefonoShow.text = contact.telephone.toString()
                contactoGroup.setOnClickListener{
                    onClick(contact)
                }
                borrarContacto.setOnLongClickListener{
                    onLongClick(contact)
                    true
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsAdapter.ShowViewHolder {
        val binding =
            ContactItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactsAdapter.ShowViewHolder(binding, onClick, onLongClick)
    }
    override fun getItemCount() = contacts.size

    override fun onBindViewHolder(holder: ContactsAdapter.ShowViewHolder, position: Int) {
        holder.bind(contacts[position], contacts.size)
    }
}