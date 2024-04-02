package com.example.weatherforecast.alerts.view

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.weatherforecast.prefernces.SharedPreference
import com.example.weatherforecast.Constants

import com.example.weatherforecast.R
import com.example.weatherforecast.alerts.AlertWorker
import com.example.weatherforecast.alerts.viewmodel.AlertViewModel
import com.example.weatherforecast.alerts.viewmodel.AlertViewModelFactory
import com.example.weatherforecast.databinding.FragmentAlertsBinding
import com.example.weatherforecast.databinding.LayoutDialogBinding
import com.example.weatherforecast.helpers.checkNetworkConnection
import com.example.weatherforecast.helpers.createDialog
import com.example.weatherforecast.helpers.formatTime
import com.example.weatherforecast.helpers.formatLong
import com.example.weatherforecast.local.LocalDataSourceImp
import com.example.weatherforecast.model.AlertState
import com.example.weatherforecast.model.ApiState
import com.example.weatherforecast.model.entity.AlertEntity
import com.example.weatherforecast.model.RepositoryImpl
import com.example.weatherforecast.network.RemoteDataSourceImpl
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds


class AlertsFragment : Fragment() , AlertsOnClickListener{

    private lateinit var binding : FragmentAlertsBinding
    private lateinit var bindingAlertLayout : LayoutDialogBinding
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory
    private lateinit var alertEntity: AlertEntity
    private lateinit var alertAdapter: AlertAdapter
    private var startTime =""
    private val TAG = "AlertsFragment"



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        setUpAlertRecyclerView()
        getAllAlerts()
        setAlert()

        lifecycleScope.launch {
            alertViewModel.isEmptyData.collectLatest {
                if(it){
                    Toast.makeText(requireContext(),"",Toast.LENGTH_SHORT).show()
                }

            }}
    }

    private fun initViewModel() {
        alertViewModelFactory = AlertViewModelFactory(RepositoryImpl.getInstance(
            RemoteDataSourceImpl.getInstance(),
            LocalDataSourceImp(requireContext())))


        alertViewModel = ViewModelProvider(this,alertViewModelFactory)[AlertViewModel::class.java]

    }

    private fun setUpAlertRecyclerView() {
        alertAdapter = AlertAdapter(requireContext(),this@AlertsFragment)
        binding.rvAlerts.adapter = alertAdapter
    }

    private fun getAllAlerts(){
        lifecycleScope.launch {
            alertViewModel.alert.collectLatest {
                when(it){
                    is AlertState.Loading ->{
                        binding.loadingAnimation.visibility = View.VISIBLE
                        binding.rvAlerts.visibility = View.GONE
                        binding.fabAddAlert.visibility = View.GONE
                        binding.layoutEmpty.visibility = View.GONE


                    }
                    is AlertState.Success ->{
                        if(it.alertEntity.isNullOrEmpty()) {
                            binding.loadingAnimation.visibility = View.GONE
                            binding.rvAlerts.visibility = View.VISIBLE
                            binding.fabAddAlert.visibility = View.VISIBLE
                            binding.layoutEmpty.visibility = View.VISIBLE
                        }else{
                            binding.loadingAnimation.visibility = View.GONE
                            binding.rvAlerts.visibility = View.VISIBLE
                            binding.fabAddAlert.visibility = View.VISIBLE
                            binding.layoutEmpty.visibility = View.GONE
                            alertAdapter.submitList(it.alertEntity)

                        }
                    }
                    else ->{

                    }
                }
            }
        }

    }

    private fun setAlert(){
        binding.fabAddAlert.setOnClickListener {
            if(checkNetworkConnection(requireContext())){
                showTimeDialog()

            }else{
                Snackbar.make(binding.root,"Please, check your network Connection", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showTimeDialog() {
        val currentTimeInMillis = System.currentTimeMillis()
        bindingAlertLayout = LayoutDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(bindingAlertLayout.root)
            alertEntity = AlertEntity()
            bindingAlertLayout.tvAlertDate.text =
                formatLong(currentTimeInMillis, "dd MMM yyyy")
            bindingAlertLayout.tvAlertTime.text =
                formatLong(currentTimeInMillis + 60 * 1000, "hh:mm a")

            bindingAlertLayout.cvAlertDate.setOnClickListener {
                showDatePicker()
            }

            bindingAlertLayout.btnCancelDialog.setOnClickListener {
                dismiss()
            }

            bindingAlertLayout.ivLoading.setOnClickListener {
                getAlertDetails()
            }

            bindingAlertLayout.btnSaveAlert.setOnClickListener {
                setAlertDelay()
                saveAlert()
                dismiss()
            }

            show()
        }
    }

    private fun saveAlert(){
        if (bindingAlertLayout.tvAlertDate.text.isNotEmpty() &&
            bindingAlertLayout.btnNotification.isChecked ||
            bindingAlertLayout.btnAlert.isChecked)
        {
            alertViewModel.hideToast()
            Log.d(TAG, "refat entity: inside")

            if(bindingAlertLayout.btnAlert.isChecked) {
                alertEntity.notification = "alarm"
                Log.d(TAG, "refat entity: alaram"+alertEntity.notification )
            }else{
                alertEntity.notification = "notification"
                Log.d(TAG, "refat entity: alaram"+alertEntity.notification )
            }
            Log.d(TAG, "refat entity: before")
            alertViewModel.insertAlert(alertEntity)
            Log.d(TAG, "refat entity: after")
        }else{
            //Toast.makeText()
            alertViewModel.showToast()
            Log.d(TAG, "refat showDialog: a777a" )
        }

    }

    private fun getAlertDetails(){
        alertViewModel.getWeather(
            SharedPreference.getInstance(requireContext()).getLatHome(),
            SharedPreference.getInstance(requireContext()).getLonHome(),
            SharedPreference.getInstance(requireContext()).getUnit(),
            SharedPreference.getInstance(requireContext()).getLanguage()
        )

        lifecycleScope.launch {
            alertViewModel.weather.collectLatest {
                when (it) {
                    is ApiState.Loading -> {
                        bindingAlertLayout.btnSaveAlert.isEnabled = false
                    }
                    is ApiState.Success -> {
                        bindingAlertLayout.btnSaveAlert.isEnabled = true
                        alertEntity.lat = it.weather.lat
                        Log.d(TAG, "refat showDialog: entityAlert.lat" + it.weather.lat)
                        alertEntity.lon = it.weather.lon
                        Log.d(TAG, "refat showDialog: entityAlert.lon" + it.weather.lon)
                        alertEntity.current = it.weather.current
                        Log.d(TAG, "refat showDialog: entityAlert.current" + it.weather.current)
                        if (!it.weather.alerts.isNullOrEmpty()) {
                            alertEntity.alert = it.weather.alerts
                            Log.d(TAG, "refat showDialog:entityAlert.alert" + it.weather.alerts)
                        }
                    }

                    else -> {}
                }
            }
        }

    }

    override fun deleteAlertItem(alertEntity: AlertEntity) {
        createDialog(
            title = getString(R.string.delete_alert),
            context = requireContext(),
            cancel = {},
            sure = { alertViewModel.deleteAlert(alertEntity) })
    }


    private fun showDatePicker() {
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraintsBuilder.build())
            .setTitleText(R.string.day_title)
            .build()

        datePicker.show(parentFragmentManager, "date")
        datePicker.addOnPositiveButtonClickListener { date ->
            val formattedDate = formatLong(date, "dd MMM yyyy")
            bindingAlertLayout.tvAlertDate.text = formattedDate
            alertEntity.startDate = formattedDate
            showTimePicker()
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val currentHourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePicker = MaterialTimePicker.Builder()
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(currentHourOfDay)
            .setMinute(currentMinute + 1)
            .setTitleText(R.string.time_title)
            .build()

        timePicker.show(parentFragmentManager, "time")

        timePicker.addOnPositiveButtonClickListener {
            val formattedTime = formatTime(timePicker.hour, timePicker.minute)
            bindingAlertLayout.tvAlertTime.text = formattedTime
            alertEntity.startTime = formattedTime
            startTime = formattedTime
            Log.d(TAG, "showTimePicker: startTime: $startTime")
        }

        timePicker.addOnCancelListener {
            val formattedTime = formatTime(timePicker.hour, timePicker.minute)
            bindingAlertLayout.tvAlertTime.text = formattedTime
        }
    }

    private fun setAlertDelay() {
        val locale = if (SharedPreference.getInstance(requireContext()).getLanguage() == "ar") {
            Locale("ar")
        } else {
            Locale("en")
        }

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("hh:mm a", locale)
        val currentTime = dateFormat.format(calendar.time)

        val startTimeValue = Time(dateFormat.parse(startTime)!!.time)
        val currentTimeValue = Time(dateFormat.parse(currentTime)!!.time)

        val delayMilliseconds = (startTimeValue.time - currentTimeValue.time).milliseconds.inWholeMilliseconds

        val inputData = Data.Builder()
            .putString(Constants.ID, alertEntity.id)
            .putLong(Constants.TIME, delayMilliseconds)
            .build()

        val myWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<AlertWorker>()
                .setInputData(inputData)
                .build()

        WorkManager.getInstance(requireContext())
            .enqueue(myWorkRequest)
    }
}