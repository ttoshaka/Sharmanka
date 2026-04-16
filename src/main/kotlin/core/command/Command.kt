package core.command

import core.models.Reply
import core.models.Event

abstract class Command(val isLongCommand: Boolean = false) {

    abstract suspend operator fun invoke(event: Event): Reply
}