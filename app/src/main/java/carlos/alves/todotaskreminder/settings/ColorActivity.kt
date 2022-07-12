package carlos.alves.todotaskreminder.settings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.MotionEvent
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.view.drawToBitmap
import androidx.core.widget.addTextChangedListener
import carlos.alves.todotaskreminder.R
import carlos.alves.todotaskreminder.databinding.ActivityColorBinding
import carlos.alves.todotaskreminder.utilities.AlertDialogBuilder
import carlos.alves.todotaskreminder.settings.SettingsConstants.*

class ColorActivity : AppCompatActivity() {

    private val binding: ActivityColorBinding by lazy { ActivityColorBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val parameter = intent.getStringExtra(PARAMETER.description)
        val color = intent.getIntExtra(COLOR.description, Integer.MAX_VALUE)

        binding.colorConstraint.setBackgroundColor(intent.getIntExtra(BACKGROUND_COLOR.description, Integer.MAX_VALUE))
        binding.colorBackButton.setBackgroundColor(intent.getIntExtra(BUTTONS_COLOR.description, Integer.MAX_VALUE))

        if (color != Integer.MAX_VALUE) {
            binding.colorView.setBackgroundColor(color)
            binding.colorRedEditText.setText(Color.red(color).toString())
            binding.colorGreenEditText.setText(Color.green(color).toString())
            binding.colorBlueEditText.setText(Color.blue(color).toString())
        }

        binding.colorBackButton.setOnClickListener {
            val backgroundColor = (binding.colorView.background as ColorDrawable).color

            val returnData = Intent()
            returnData.putExtra(COLOR.description, Color.rgb(backgroundColor.red, backgroundColor.green, backgroundColor.blue))
            returnData.putExtra(PARAMETER.description, parameter)

            setResult(RESULT_OK, returnData)
            finish()
        }

        binding.colorRedEditText.addTextChangedListener {
            val intColorCode = colorCodeValidationAndConversion(it!!)
            if (intColorCode != null) {
                drawColor(intColorCode, null, null)
            }
        }

        binding.colorGreenEditText.addTextChangedListener {
            val intColorCode = colorCodeValidationAndConversion(it!!)
            if (intColorCode != null) {
                drawColor(null, intColorCode, null)
            }

        }
        binding.colorBlueEditText.addTextChangedListener {
            val intColorCode = colorCodeValidationAndConversion(it!!)
            if (intColorCode != null) {
                drawColor(null, null, intColorCode)
            }
        }

        binding.colorImageView.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                val bitmap = view.drawToBitmap()
                val pixel = bitmap.getPixel(event.getX().toInt(), event.getY().toInt())

                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)

                binding.colorRedEditText.setText(red.toString())
                binding.colorGreenEditText.setText(green.toString())
                binding.colorBlueEditText.setText(blue.toString())
            }
            true
        }
    }

    private fun isColorCodeWithinRange(intColorCode: Int): Boolean = intColorCode in 0..255

    private fun colorCodeValidationAndConversion(colorCodeEditable: Editable): Int? {
        val colorCode = colorCodeEditable.toString()
        if (colorCode.isNotBlank()) {
            val intColorCode = colorCode.toInt()
            if (isColorCodeWithinRange(intColorCode)) {
                return intColorCode
            } else {
                AlertDialogBuilder.generateErrorDialog(this, R.string.color_value_range)
            }
        }
        return null
    }

    private fun drawColor(red: Int?, green: Int?, blue: Int?) {
        val backgroundColor = (binding.colorView.background as ColorDrawable).color
        val backgroundRed = backgroundColor.red
        val backgroundGreen = backgroundColor.green
        val backgroundBlue = backgroundColor.blue

        binding.colorView.setBackgroundColor(
            Color.rgb(
                red ?: backgroundRed,
                green ?: backgroundGreen,
                blue ?: backgroundBlue
            )
        )
    }
}
