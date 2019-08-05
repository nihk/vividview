package ca.nihk.scalerfader

import androidx.annotation.VisibleForTesting

object ServiceLocator {

    var managerProvider: (ScalerFader) -> Manager = { Manager(it, it) }
        @VisibleForTesting set
}