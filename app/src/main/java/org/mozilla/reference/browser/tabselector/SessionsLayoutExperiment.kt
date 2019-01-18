package org.mozilla.reference.browser.tabselector

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.PARENT_ID
import kotlinx.android.synthetic.main.component_sessions.*
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.BOTTOM
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.END
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.START
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.TOP
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.support.v4.dip
import org.mozilla.reference.browser.AATestDescriptor
import org.mozilla.reference.browser.isInExperiment

internal fun SessionsFragment.layoutUIComponents(layout: ConstraintLayout) {
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

internal fun SessionsFragment.setInExperimentConstraints(layout: ConstraintLayout) {
    layout.applyConstraintSet {
        sessions_layout {
            connect(
                    TOP to TOP of PARENT_ID margin dip(10),
                    START to START of PARENT_ID margin dip(10),
                    END to END of PARENT_ID margin dip(10)
            )
        }
    }
    sessions_layout.applyConstraintSet {
        fenix_title {
            connect(
                    TOP to TOP of PARENT_ID,
                    END to END of PARENT_ID
            )
        }
        recycler_sessions {
            connect(
                    TOP to BOTTOM of fenix_title
            )
        }
    }
}

internal fun SessionsFragment.setOutOfExperimentConstraints(layout: ConstraintLayout) {
    layout.applyConstraintSet {
        sessions_layout {
            connect(
                    BOTTOM to BOTTOM of PARENT_ID margin dip(10),
                    START to START of PARENT_ID margin dip(10),
                    END to END of PARENT_ID margin dip(10)
            )
        }
    }
    sessions_layout.applyConstraintSet {
        fenix_title {
            connect(
                    BOTTOM to BOTTOM of PARENT_ID,
                    START to START of PARENT_ID
            )
        }
        recycler_sessions {
            connect(
                    BOTTOM to TOP of fenix_title
            )
        }
    }
}