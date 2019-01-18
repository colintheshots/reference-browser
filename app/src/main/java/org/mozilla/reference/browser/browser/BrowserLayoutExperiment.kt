package org.mozilla.reference.browser.browser

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.PARENT_ID
import kotlinx.android.synthetic.main.fragment_browser.*
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.BOTTOM
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.END
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.START
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.TOP
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.mozilla.reference.browser.AATestDescriptor
import org.mozilla.reference.browser.isInExperiment

internal fun BrowserFragment.layoutUIComponents(layout: ConstraintLayout) {
    context?.let {
        when {
            it.isInExperiment(AATestDescriptor) -> {
                setInExperimentConstraints(layout)
            }
            else -> {
                setOutOfExperimentConstraints(layout)
            }
        }
    } // if context is null, we're unattached and shouldn't layout
}

internal fun BrowserFragment.setInExperimentConstraints(layout: ConstraintLayout) {
    layout.applyConstraintSet {
        engineView.asView().id {
            connect(
                    TOP to TOP of PARENT_ID,
                    START to START of PARENT_ID
            )
        }
        awesomeBar {
            connect(
                    TOP to TOP of PARENT_ID,
                    START to START of PARENT_ID
            )
        }
        button_show_sessions {
            connect(
                    BOTTOM to BOTTOM of PARENT_ID,
                    START to START of PARENT_ID
            )
        }

    }
}

internal fun BrowserFragment.setOutOfExperimentConstraints(layout: ConstraintLayout) {
    layout.applyConstraintSet {
        engineView.asView().id {
            connect(
                    TOP to TOP of PARENT_ID,
                    START to START of PARENT_ID
            )
        }
        awesomeBar {
            connect(
                    TOP to TOP of PARENT_ID,
                    START to START of PARENT_ID
            )
        }
        button_show_sessions {
            connect(
                    BOTTOM to BOTTOM of PARENT_ID,
                    END to END of PARENT_ID
            )
        }
    }
}