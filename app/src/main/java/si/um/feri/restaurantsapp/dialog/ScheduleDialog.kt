package si.um.feri.restaurantsapp.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import si.um.feri.restaurantsapp.R
import si.um.feri.restaurantsapp.models.ScheduleWithDay

class ScheduleDialog : DialogFragment() {

    fun newInstance(fullSchedule: List<ScheduleWithDay>): ScheduleDialog {
        val scheduleDialog = ScheduleDialog()
        val bundle = Bundle()
        val fullScheduleArrayList = arrayListOf<ScheduleWithDay>()
        fullScheduleArrayList.addAll(fullSchedule)
        bundle.putParcelableArrayList("fullSchedule", fullScheduleArrayList)
        scheduleDialog.arguments = bundle
        return scheduleDialog
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val title = TextView(requireContext())
            title.text = getString(R.string.dialog_restaurant_name)
            title.setBackgroundColor(Color.rgb(250, 180, 120))
            title.setPadding(10, 10, 10, 10)
            title.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                resources.getDimension(R.dimen.dialog_title_text_size))
            title.gravity = Gravity.CENTER

            builder.setCustomTitle(title)

            val content = TextView(requireContext())

            val bundle = arguments
            val fullSchedule = bundle?.getParcelableArrayList<ScheduleWithDay>("fullSchedule")
            content.text = "\n"

            for (schedule in fullSchedule!!) {
                if (schedule.day.dayId != 8)
                    content.text = "${content.text} ${schedule.day.name}: ${schedule.startTime} - ${schedule.endTime}\n"
                else
                    content.text = "${content.text} ${schedule.day.name}: ${schedule.startTime} - ${schedule.endTime}"
            }

            content.gravity = Gravity.CENTER
            content.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                resources.getDimension(R.dimen.dialog_title_text_size))

            builder.setView(content)

            builder.setPositiveButton(
                "Zapri"
            ) { dialog, _ -> dialog.dismiss() }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}