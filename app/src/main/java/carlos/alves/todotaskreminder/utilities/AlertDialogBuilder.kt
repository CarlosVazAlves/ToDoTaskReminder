package carlos.alves.todotaskreminder.utilities

import android.app.AlertDialog
import android.content.Context
import carlos.alves.todotaskreminder.R

class AlertDialogBuilder {

    companion object {
        fun generateErrorDialog(context: Context, messageResource: Int) {
            fireDialog(context, R.string.error, messageResource)
        }

        private fun fireDialog(context: Context, title: Int, message: Int) {
            AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .show()
        }
    }
}