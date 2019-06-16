package si.um.feri.restaurantsapp.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_no_internet.*
import kotlinx.android.synthetic.main.fragment_no_internet.view.*
import si.um.feri.restaurantsapp.LoadingSpinnerActivity
import si.um.feri.restaurantsapp.MainActivity
import si.um.feri.restaurantsapp.R

private const val ACTIVITY = "activity"
private const val MAIN_ACTIVITY = 1

class NoInternetFragment : Fragment() {

    companion object {
        fun newInstance(): NoInternetFragment = NoInternetFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_no_internet, container, false)
    }

    @Suppress("RedundantOverride")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swiperefresh.setOnRefreshListener {
            val intent = Intent(requireActivity(), LoadingSpinnerActivity::class.java)
            intent.putExtra(ACTIVITY, MAIN_ACTIVITY)
            requireActivity().finish()
            startActivity(intent)
        }
    }
}