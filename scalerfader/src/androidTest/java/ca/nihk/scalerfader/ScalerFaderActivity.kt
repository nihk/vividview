package ca.nihk.scalerfader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ScalerFaderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ScalerFaderProvider.scalerFader)
    }
}