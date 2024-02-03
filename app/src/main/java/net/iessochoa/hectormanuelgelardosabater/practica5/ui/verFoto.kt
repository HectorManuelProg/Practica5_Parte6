package net.iessochoa.hectormanuelgelardosabater.practica5

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.iessochoa.hectormanuelgelardosabater.practica5.databinding.FragmentVerFotoBinding

class VerFoto : Fragment() {

    private var _binding: FragmentVerFotoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerFotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fotoUri = arguments?.getString("fotoUri")
        binding.ivVerFoto.setImageURI(Uri.parse(fotoUri))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}