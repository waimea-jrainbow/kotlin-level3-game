import com.formdev.flatlaf.themes.FlatMacDarkLaf
import java.awt.Color
import java.awt.Font
import javax.swing.*

class Interactable(
    val name: String,
    val description: String,
    val isPuzzle: Boolean = false

) {
    private var examined: Boolean = false
    var solved = false

    fun examine() {
        examined = true
    }

    override fun toString() = name
}

class Room(
    val name: String,
    val description: String,
    var interactables: MutableList<Interactable> = mutableListOf()


) {
    val exits: MutableList<Room> = mutableListOf()


    fun addExit(exit: Room) {
        exits.add(exit)

    }

    fun addinteractable(interactable: Interactable) {
        interactables.add(interactable)
    }
}

/**
 * Application entry point
 */
fun main() {
    FlatMacDarkLaf.setup()          // Initialise the LAF

    val app = App()                 // Get an app state object
    val window = MainWindow(app)    // Spawn the UI, passing in the app state

    SwingUtilities.invokeLater { window.show() }
}


/**
 * Manage app state
 *
 */
class App {
    var currentRoom: Room
    val rooms: MutableList<Room> = mutableListOf()
    var currentInteractable: Interactable? = null
    var canMove = true

    init {
        setUpRooms()
        setUpinteractables(rooms)
        currentRoom = rooms[0]
        println(currentRoom.name)
    }

    fun setUpRooms() {
        val cell = Room(
            "Cell 01",
            """<html><wrap>
                                   CELL 01 - HOLDING BAY
                                   =========================================== <br>
                                   The walls hum with the low vibration of the station's engines. A metal bunk 
                                   is bolted to the left wall, its surface scratched with years of graffiti. 
                                   Above a ventilation panel on the far wall, six indicator lights are mounted 
                                   in a row — three glow steady blue, one pulses weakly, two are dark. 
                                   A faint smell of recycled air. Somewhere distant, a klaxon sounds and fades.
                                   There is a small maintenance panel set into the wall.
                                   </wrap></html>""",
        )

        val guardRoom = Room(
            "GUARD STATION",
            """<html><wrap>
                                   GUARD STATION — SECTOR 4 HUB
                                   ============================== <br>
                                   A cramped operations room. Banks of monitors line one wall, most dark or
                                   showing static. A guard's workstation dominates the center — a physical
                                   console with switches, levers, and a terminal that's still live, its screen
                                   casting pale green light. A star map is pinned to the far wall. Three heavy
                                   doors lead off in different directions, all sealed.</html></wrap>"""
        )

        val cargoBay = Room(
            "CARGO BAY",
            """<html><wrap>      
                                   CARGO BAY — STORAGE WING C
                                   ============================ <br>
                                   A wide, low-ceilinged room lined with magnetic clamping racks. Crates of
                                   various sizes are locked to the walls and floor in neat rows, each labeled
                                   with a stenciled symbol rather than text — station protocol, apparently.
                                   A manifest terminal on the near wall flickers with corrupted data. A tool
                                   locker stands open in the corner, mostly stripped bare. The room smells
                                   faintly of machine oil and something burnt."""
        )

        val reactorRoom = Room(
            "REACTOR ROOM",
            """<html>
                                   REACTOR CORE — RESTRICTED ZONE
                                   ================================ <br>
                                   Heat hits you immediately. The room pulses with a deep, rhythmic vibration.
                                   A cylindrical reactor housing dominates the center, its casing streaked
                                   with scorch marks. Two thick conduit pipes run from the base — one labeled
                                   INTAKE, one labeled EXHAUST — each controlled by a large lever on the wall.
                                   Both levers are currently in the OPEN position. A warning light strobes red.
                                   <br>
                                   On the wall beside the levers, a placard reads:
                                   !! PLASMA VENT PROTOCOL !!
                                   NEVER OPEN BOTH SIMULTANEOUSLY.
                                   ALWAYS CLOSE EXHAUST BEFORE INTAKE.
                                   FAILURE TO COMPLY WILL RESULT IN CORE OVERLOAD.
                                   <br>
                                   A hazard gauge on the reactor face ticks upward: 40%... 41%...
                                   <br>
                                   You have maybe two minutes before this gets very bad.<wrap>"""
        )

        val medBay = Room(
            "MED BAY",
            """<html>
                                   MEDICAL BAY — WARD B<br>
                                   ===================== <br>
                                   Cool and dim compared to the rest of the station. Two recovery beds are
                                   bolted to the floor, partitioned by a thin curtain. Medical monitors hum
                                   above each bed, displaying vitals. One bed is empty. The other is occupied.
                                   <br><br>
                                   A figure stirs as you enter — a prisoner, like you, in the same grey
                                   jumpsuit. One arm is in a makeshift splint. They look at you with
                                   exhausted, cautious eyes.
                                   <br><br>
                                   DR. YARA SONG (it says on a faded name tag, though the title seems
                                   optimistic given the surroundings) says nothing at first. Just watches.<wrap><html>"""
        )

        val airLock = Room(
            "AIR LOCK",
            """<html><wrap>
                                   AIRLOCK — DOCKING RING 3
                                   ========================= <br>
                                   A heavy pressure door dominates the far wall, its surface scratched and
                                   dented from years of use. Beside it: a biometric scanner, dark and
                                   inert. On the near wall, a manual override panel sits behind a sealed
                                   metal plate, its edges fused — someone welded it shut deliberately.
                                   A small porthole to the right shows open space: stars, and the dim
                                   shape of a drifting salvage shuttle about two hundred meters out.

                                   The scanner is dark. The override panel is inaccessible.
                                   You're close. Not there yet.</html>"""
        )
        rooms.add(cell)
        rooms.add(guardRoom)
        rooms.add(reactorRoom)
        rooms.add(medBay)
        rooms.add(airLock)

        cell.addExit(guardRoom)

        guardRoom.addExit(cell)
        guardRoom.addExit(cargoBay)
        guardRoom.addExit(reactorRoom)
        guardRoom.addExit(medBay)

        cargoBay.addExit(guardRoom)

        reactorRoom.addExit(guardRoom)
        reactorRoom.addExit(airLock)

        medBay.addExit(guardRoom)
        medBay.addExit(airLock)

        airLock.addExit(reactorRoom)
        airLock.addExit(medBay)


    }

    private fun setUpinteractables(rooms: MutableList<Room>) {
        val bunk = Interactable(
            "Bunk",
            """<html><wrap>The bunk is bolted solid - you're not pulling it free. But the surface is
                                      covered in scratches. One message stands out, carved deeper than the rest:
                                      "THE STARS DON'T LIE — 3 BRIGHT, 1 DIM, 2 GONE."
                                      Someone was trying to leave a message for whoever came next."""
        )
        val ventLights = Interactable(
            "Vent lights", """<html><wrap>Six lights above the vent panel. From left to right:<br>
                                                                            [ON] [ON] [ON] [DIM] [OFF] [OFF]<br>
                                                                            Three steady, one dim, two dead."""
        )
        val cellDoor = Interactable(
            "Cell Door", """<html><wrap>A heavy mag-locked security door. A keypad beside it is dark — no power
                                                                        running to it from this side. You're not getting out that way."""
        )
        val panel = Interactable(
            "Panel", """<html><wrap>A maintenance panel set into the wall beneath the lights. It has a small
            3-digit keypad with a magnetic lock. If you could open this...""", true
        )

        rooms[0].addinteractable(bunk)
        rooms[0].addinteractable(ventLights)
        rooms[0].addinteractable(cellDoor)
        rooms[0].addinteractable(panel)

    }

    fun interactableSolved() {
        currentInteractable?.solved = true
    }

    fun currentInteractableSolved(): Boolean {
        return currentInteractable?.solved ?: false
    }

    fun moveRoom(room: Room) {
        currentRoom = room
    }

}


/**
 * Main UI window, handles user clicks, etc.
 *
 * @param app the app state object
 */
class MainWindow(val app: App) {
    val frame = JFrame("Space escape")
    private val panel = JPanel().apply { layout = null }

    private val currentRoomDescLabel = JLabel(app.currentRoom.description)
    private val exit1Button = JButton()
    private val exit2Button = JButton()
    private val exit3Button = JButton()
    private val exit4Button = JButton()
    private val examinedInteractableDescLabel = JLabel()

    private var examineListLabel = JLabel("Select to examine")
    private val examineListModel = DefaultListModel<Interactable>()
    private val examineList = JList(examineListModel)

    private val infoWindow = Puzzle1Window(this, app)


    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()

        exit1Button.isVisible = false
        exit2Button.isVisible = false
        exit3Button.isVisible = false
        exit4Button.isVisible = false

        updateUI()
    }

    private fun setupLayout() {
        panel.preferredSize = java.awt.Dimension(600, 700)


        currentRoomDescLabel.setBounds(30, 0, 300, 300)
        exit1Button.setBounds(30, 350, 100, 50)
        exit2Button.setBounds(150, 350, 100, 50)
        exit3Button.setBounds(270, 350, 100, 50)
        exit4Button.setBounds(390, 350, 100, 50)
        examineListLabel.setBounds(350, 5, 150, 30)
        examineList.setBounds(350, 35, 150, 290)
        examinedInteractableDescLabel.setBounds(30, 420, 450, 160)

        panel.add(currentRoomDescLabel)
        panel.add(exit1Button)
        panel.add(exit2Button)
        panel.add(exit3Button)
        panel.add(exit4Button)
        panel.add(examineListLabel)
        panel.add(examineList)
        panel.add(examinedInteractableDescLabel)


    }

    private fun setupStyles() {

        currentRoomDescLabel.font = Font(Font.SANS_SERIF, Font.BOLD, 12)

        exit1Button.font = Font(Font.SANS_SERIF, Font.BOLD, 8)
        exit2Button.font = Font(Font.SANS_SERIF, Font.BOLD, 8)
        exit3Button.font = Font(Font.SANS_SERIF, Font.BOLD, 8)
        exit4Button.font = Font(Font.SANS_SERIF, Font.BOLD, 8)
        examineListLabel.font = Font(Font.SANS_SERIF, Font.BOLD, 12)
        examineList.font = Font(Font.SANS_SERIF, Font.BOLD, 12)
    }

    private fun setupWindow() {
        frame.isResizable = true                         // Can't resize
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE  // Exit upon window close
        frame.contentPane = panel                           // Define the main content
        frame.pack()
        frame.setLocationRelativeTo(null)                   // Centre on the screen
    }

    private fun setupActions() {
        exit1Button.addActionListener { handleExit1Click() }
        exit2Button.addActionListener { handleExit2Click() }
        exit3Button.addActionListener { handleExit3Click() }
        exit4Button.addActionListener { handleExit4Click() }

        examineList.addListSelectionListener { event ->
            if (!event.valueIsAdjusting) {
                val selected = examineList.selectedValue
                if (selected != null) {
                    examinedInteractableDescLabel.text = selected.description
                    selected.examine()
                    examineList.clearSelection()

                    app.currentInteractable = selected

                    if (selected.name == "Panel") {
                        infoWindow.show()
                    }
                }
            }
        }
    }

    private fun handleExit1Click() {
        app.moveRoom(app.currentRoom.exits[0])       // Update the app state
        updateUI()                  // Update this window UI to reflect this
        println(app.currentRoom.name)
    }

    private fun handleExit2Click() {
        app.moveRoom(app.currentRoom.exits[1])       // Update the app state
        updateUI()                  // Update this window UI to reflect this
        println(app.currentRoom.name)
    }

    private fun handleExit3Click() {
        app.moveRoom(app.currentRoom.exits[2])
        updateUI()
        println(app.currentRoom.name)
    }

    private fun handleExit4Click() {
        app.moveRoom(app.currentRoom.exits[3])
        updateUI()
        println(app.currentRoom.name)
    }


    fun updateUI() {

        app.currentRoom.interactables.forEach{ interactable ->
            if (interactable.isPuzzle && !interactable.solved)
                app.canMove = false
        }

        currentRoomDescLabel.text = app.currentRoom.description
        exit1Button.text = app.currentRoom.exits[0].name
        exit2Button.text = if (app.currentRoom.exits.size > 1) app.currentRoom.exits[1].name else ""
        exit3Button.text = if (app.currentRoom.exits.size > 2) app.currentRoom.exits[2].name else ""
        exit4Button.text = if (app.currentRoom.exits.size > 3) app.currentRoom.exits[3].name else ""

        exit1Button.isVisible = app.canMove
        exit2Button.isVisible = app.currentRoom.exits.size > 1
        exit3Button.isVisible = app.currentRoom.exits.size > 2
        exit4Button.isVisible = app.currentRoom.exits.size > 3

        examineListModel.clear()
        app.currentRoom.interactables.forEach { interactable ->
            examineListModel.addElement(interactable)
        }







    }

    fun show() {
        frame.isVisible = true
    }
}


/**
 * Info UI window is a child dialog and shows how the
 * app state can be shown / updated from multiple places
 *
 * @param owner the parent frame, used to position and layer the dialog correctly
 * @param app the app state object
 */
class Puzzle1Window(val owner: MainWindow, val app: App) {
    private val dialog = JDialog(owner.frame, "MAINTENANCE PANEL", false)
    private val panel = JPanel().apply { layout = null }

    private val enteredCodeLabel = JLabel("")
    private var codeFeedbackLabel = JLabel("")
    private val button1 = JButton("1")
    private val button2 = JButton("2")
    private val button3 = JButton("3")
    private val buttonClr = JButton("CLR")
    private val buttonOK = JButton("OK")
    private var enteredCode = mutableListOf<Int>()
    private var code = "312"

    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
    }

    private fun setupLayout() {
        panel.preferredSize = java.awt.Dimension(240, 180)


        enteredCodeLabel.setBounds(60, 5, 180, 30)
        codeFeedbackLabel.setBounds(60, 20, 180, 30)

        button1.setBounds(0, 50, 60, 60)
        button2.setBounds(60, 50, 60, 60)
        button3.setBounds(120, 50, 60, 60)
        buttonClr.setBounds(0, 110, 60, 60)
        buttonOK.setBounds(80, 110, 60, 60)


        panel.add(enteredCodeLabel)
        panel.add(codeFeedbackLabel)
        panel.add(button1)
        panel.add(button2)
        panel.add(button3)
        panel.add(buttonClr)
        panel.add(buttonOK)
    }

    private fun setupStyles() {
        enteredCodeLabel.font = Font(Font.DIALOG_INPUT, Font.BOLD, 16)
        codeFeedbackLabel.font = Font(Font.DIALOG_INPUT, Font.PLAIN, 16)


    }

    private fun setupWindow() {
        dialog.isResizable = false                              // Can't resize
        dialog.defaultCloseOperation = JDialog.HIDE_ON_CLOSE    // Hide upon window close
        dialog.contentPane = panel                              // Main content panel
        dialog.pack()
    }

    private fun setupActions() {
        button1.addActionListener { handle1Click() }
        button2.addActionListener { handle2Click() }
        button3.addActionListener { handle3Click() }
        buttonClr.addActionListener { handleClrClick() }
        buttonOK.addActionListener { checkCode() }
    }

    private fun handle1Click() {
        enteredCode.add(1)
        println(enteredCode.toString())
        if (enteredCode.size == 3) checkCode()
        updateUI()
    }

    private fun handle2Click() {
        enteredCode.add(2)
        println(enteredCode.toString())
        if (enteredCode.size == 3) checkCode()
        updateUI()
    }

    private fun handle3Click() {
        enteredCode.add(3)
        println(enteredCode.toString())
        if (enteredCode.size == 3) checkCode()
        updateUI()
    }

    private fun checkCode() {
        println(enteredCode.toString())
        if (enteredCode.joinToString("") == code) {
            app.interactableSolved()
            codeStatusText()
            val closeTimer = Timer(900) {
                println(enteredCode.toString())
                println("correct")
                dialog.dispose()
            }
            closeTimer.isRepeats = false
            closeTimer.start()
            owner.updateUI()
        } else {
            codeStatusText()
            val closeTimer = Timer(900) {
                enteredCode.clear()
                codeFeedbackLabel.text = ""
                updateUI()
            }
            closeTimer.isRepeats = false
            closeTimer.start()
        }

    }



    private fun codeStatusText() {
        if (app.currentInteractableSolved()) {
            codeFeedbackLabel.text = "Correct"
            codeFeedbackLabel.foreground = Color.green
        } else {
            codeFeedbackLabel.text = "Incorrect"
            codeFeedbackLabel.foreground = Color.red
        }

    }

    fun show() {
        val ownerBounds = owner.frame.bounds          // get location of the main window
        dialog.setLocation(                           // Position next to main window
            ownerBounds.x + ownerBounds.width + 10,
            ownerBounds.y
        )

        if (!app.currentInteractableSolved()) dialog.isVisible = true
    }

    private fun updateUI() {
        val codeForDisplay = List(3) { number ->
            if (number < enteredCode.size) enteredCode[number].toString() else "_"
        }
        enteredCodeLabel.text = codeForDisplay.joinToString("-")
    }

    private fun handleClrClick() {
        enteredCode.clear()
        updateUI()
    }


}