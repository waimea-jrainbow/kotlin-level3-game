import com.formdev.flatlaf.themes.FlatMacDarkLaf
import java.awt.Color
import java.awt.Font
import javax.swing.*
import javax.xml.crypto.dsig.Manifest


/**
 * Represents an object within a Room that the player can examine or interact with.
 *
 * Interactables may be objects with descriptions (e.g. a bunk or a door) or puzzle
 * objects that must be solved before the player can progress. Each interactable tracks
 * whether it has been examined and, if it is a puzzle, whether it has been solved.
 *
 * @property name        A short display name shown in the examine list (e.g. "Bunk", "Panel").
 * @property description The flavour text shown to the player when the object is examined.
 * @property isPuzzle    Whether this interactable contains a puzzle that blocks progression.
 *                       Defaults to `false`.
 */
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

/**
 * Represents a location in the game world that the player can enter and occupy.
 *
 * Each room has a name, a description that is shown to the player, a list of
 * Interactable objects the player can examine, and a list of connected Room exits
 * the player can move to.
 *
 * @property name          The short display name of the room (e.g. "GUARD STATION").
 * @property description   The description shown to the player upon entering.
 * @property interactables The mutable list of objects present in the room.
 */
class Room(
    val name: String,
    val description: String,
    var interactables: MutableList<Interactable> = mutableListOf(),
    var visited: Boolean = false


) {
    val exits: MutableList<Room> = mutableListOf()


    fun addExit(exit: Room) {
        exits.add(exit)

    }

    fun addInteractable(interactable: Interactable) {
        interactables.add(interactable)
    }
}

/**
 * Application entry point.
 *
 * Initializes the FlatMacDark look-and-feel, constructs the App state object and the
 * MainWindow UI, then schedules the window to become visible.
 */
fun main() {
    FlatMacDarkLaf.setup()          // Initialise the LAF

    val app = App()                 // Get an app state object
    val window = MainWindow(app)    // Spawn the UI, passing in the app state

    SwingUtilities.invokeLater { window.show() }
}


/**
 * Central game-state that holds all rooms, tracks the player's current location,
 * and coordinates puzzle progression.
 *
 * App is constructed once at startup. It builds and connects all Room instances,
 * populates them with Interactable objects.
 *
 * @property currentRoom          The room the player is currently in.
 * @property rooms                The ordered list of all rooms in the game.
 * @property currentInteractable  The Interactable most recently selected by the player,
 *                                or `null` if none has been selected yet.
 * @property canMove              Whether the player is currently allowed to move between
 *                                rooms. Set to `false` while an unsolved puzzle blocks exit.
 */
class App {
    var currentRoom: Room
    val rooms: MutableList<Room> = mutableListOf()
    var currentInteractable: Interactable? = null
    var canMove = true

    init {
        setUpRooms()
        setUpInteractables(rooms)
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
        rooms.add(cargoBay)
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

    private fun setUpInteractables(rooms: MutableList<Room>) {
        // Cell setup
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




        rooms[0].addInteractable(bunk)
        rooms[0].addInteractable(ventLights)
        rooms[0].addInteractable(cellDoor)
        rooms[0].addInteractable(panel)

        //Guard setup
        val console = Interactable(
            "Console", """<html><wrap>The main workstation. A terminal screen glows with a login prompt, the keyboard is locked
                                                          however to the side there is a keypad labelled 
                                                          <br><br>
                                                          DOOR CONTROL 
                                                          <br><br>
                                                          Scratched into the keypad is a message
                                                          "Look to the stars and you will find the key"
                                                          """, true
        )

        val starMap = Interactable(
            "Star map", """<html><wrap>A navigation chart of the sector pinned to the wall. Several constellations
                                               are marked. One is circled in red marker
                                               a horizontal line of <strong>three</strong> stars, a branch down to another line of <strong>six</strong> stars then 
                                               another branch down to a line of <strong>five</strong> stars
                                               """
        )


        val desk = Interactable("Desk", """<html><wrap>A standard-issue guard's desk. Empty coffee bulb, a duty roster you don't
                                                               care about, a personal photo face-down. The top drawer is locked. The
                                                               bottom drawer slides open inside is a guard ID card"""
        )

        val photo = Interactable("Photo","""<html><wrap>You flip it over. A guard in dress uniform, smiling with a family.
                                                                You set it back down, face-up this time.""")

        val doors = Interactable("Doors", """<html><wrap>Three reinforced security doors. Door A is marked REACTOR - AUTHORIZED
                                                                             PERSONNEL ONLY. Door B is marked MEDICAL. Both are sealed. The corridor
                                                                             to the cargo bay has no door just an open arch.""")

        rooms[1].addInteractable(console)
        rooms[1].addInteractable(starMap)
        rooms[1].addInteractable(desk)
        rooms[1].addInteractable(photo)
        rooms[1].addInteractable(desk)
        rooms[1].addInteractable(doors)

        //Cargo Bay setup

        val manifest = Interactable("Manifest", """<html><wrap>A computer monitor lists the contents of the cargo 
                                                                       containers stacked around the room.<br>
                                                                       Crate 1 - Rations<br>
                                                                       Crate 2 - Medical supplies<br>
                                                                       Crate 3 - Laser cutter<br>
                                                                       Crate 4 - Meat substitute™ - Sealed for peak freshness<br>
                                                                       Crate 5 - Life raft<br>
                                                                       Crate 6 - Cool Coolant™ - Chill out man 
                                                                       After this the size of the monitor cuts of the next line.
                                                                       You can't find a way to scroll down.
                                                                       """)

        val crate1 = Interactable("Crate1","""<html><wrap>An open crate containing sealed ration packs"""")

        val crate2 = Interactable("Crate 2","""<html><wrap>"""")


        rooms[2].addInteractable(manifest)
        rooms[2].addInteractable(crate1)



    }

    fun interactableSolved() {
        currentInteractable?.solved = true
    }

    fun currentInteractableSolved(): Boolean {
        return currentInteractable?.solved ?: false
    }

    fun moveRoom(room: Room) {
        currentRoom.visited = true
        currentRoom = room
    }

}


/**
 * The primary application window shows the current room, its exits and its examinable objects
 *
 * @param app The shared App state object that this window reads from and writes to.
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

    private val puzzle1Window = PuzzleWindow(this, app, "312")
    private val puzzle2Window = PuzzleWindow(this, app, "365")


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

                    when (selected.name) {
                        "Panel" -> {puzzle1Window.show(selected)
                                    frame.focusableWindowState = false}

                        "Console" -> puzzle2Window.show(selected)

                        else -> null
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
        app.canMove = true
        val exits = app.currentRoom.exits

        app.currentRoom.interactables.forEach { interactable ->
            if (interactable.isPuzzle && !interactable.solved)
                app.canMove = false
        }

        currentRoomDescLabel.text = app.currentRoom.description
        exit1Button.text = if (app.currentRoom.exits.isNotEmpty()) app.currentRoom.exits[0].name else ""
        exit2Button.text = if (app.currentRoom.exits.size > 1) app.currentRoom.exits[1].name else ""
        exit3Button.text = if (app.currentRoom.exits.size > 2) app.currentRoom.exits[2].name else ""
        exit4Button.text = if (app.currentRoom.exits.size > 3) app.currentRoom.exits[3].name else ""

        exit1Button.isVisible = exits.isNotEmpty() && (app.canMove || exits[0].visited)
        exit2Button.isVisible = exits.size > 1 && (app.canMove || exits[1].visited)
        exit3Button.isVisible = exits.size > 2 && (app.canMove || exits[2].visited)
        exit4Button.isVisible = exits.size > 3 && (app.canMove || exits[3].visited)

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
 * A JDialog that presents the maintenance-panel number puzzle to the player.
 *
 * The puzzle displays a three-digit code interface with buttons for the digits 1–3,
 * a clear button, and a confirm button. The correct code is derived from the clues left
 * in the cell (the vent lights: three ON, one DIM, two OFF → "312"). On correct entry
 * the dialog closes automatically after a short delay and marks the associated
 * Interactable as solved via App.interactableSolved, which then allows the player to move
 * the next room
 *
 * @param owner The MainWindow that owns this dialog, used for positioning.
 * @param app   The shared App state object used to check and update puzzle state.
 * @param code The correct code for the puzzle
 */
class PuzzleWindow(val owner: MainWindow, val app: App, val code:String) {
    private val dialog = JDialog(owner.frame, "Enter code", false)
    private val panel = JPanel().apply { layout = null }

    var targetInteractable: Interactable? = null

    private val enteredCodeLabel = JLabel("")
    private var codeFeedbackLabel = JLabel("")
    private val button1 = JButton("1")
    private val button2 = JButton("2")
    private val button3 = JButton("3")
    private val button4 = JButton("4")
    private val button5 = JButton("5")
    private val button6 = JButton("6")
    private val button7 = JButton("7")
    private val button8 = JButton("8")
    private val button9 = JButton("9")
    private val buttonClr = JButton("CLR")
    private val buttonOK = JButton("OK")
    private var enteredCode = mutableListOf<Int>()

    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
    }

    private fun setupLayout() {
        panel.preferredSize = java.awt.Dimension(240, 320)


        enteredCodeLabel.setBounds(70, 5, 2200, 30)
        codeFeedbackLabel.setBounds(10, 30, 220, 30)

        button1.setBounds(0,   65, 60, 60)
        button2.setBounds(60,  65, 60, 60)
        button3.setBounds(120, 65, 60, 60)
        button4.setBounds(0,   125, 60, 60)
        button5.setBounds(60,  125, 60, 60)
        button6.setBounds(120, 125, 60, 60)
        button7.setBounds(0,   185, 60, 60)
        button8.setBounds(60,  185, 60, 60)
        button9.setBounds(120, 185, 60, 60)
        buttonClr.setBounds(0,   245, 80, 60)
        buttonOK.setBounds(100, 245, 80, 60)


        panel.add(enteredCodeLabel)
        panel.add(codeFeedbackLabel)
        panel.add(button1)
        panel.add(button2)
        panel.add(button3)
        panel.add(button4)
        panel.add(button5)
        panel.add(button6)
        panel.add(button7)
        panel.add(button8)
        panel.add(button9)
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
        button1.addActionListener { handleNumClick(1) }
        button2.addActionListener { handleNumClick(2) }
        button3.addActionListener { handleNumClick(3) }
        button4.addActionListener { handleNumClick(4) }
        button5.addActionListener { handleNumClick(5) }
        button6.addActionListener { handleNumClick(6) }
        button7.addActionListener { handleNumClick(7) }
        button8.addActionListener { handleNumClick(8) }
        button9.addActionListener { handleNumClick(9) }
        buttonClr.addActionListener { handleClrClick() }
        buttonOK.addActionListener { checkCode() }
    }

    private fun handleNumClick(number:Int) {
        enteredCode.add(number)
        println(enteredCode.toString())
        if (enteredCode.size == 3) checkCode()
        updateUI()
    }


    private fun checkCode() {
        println(enteredCode.toString())
        if (enteredCode.joinToString("") == code) {
            targetInteractable?.solved = true
            codeStatusText()
            val closeTimer = Timer(900) {
                println(enteredCode.toString())
                println("correct")
                dialog.dispose()
                enteredCode.clear()
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
        if (targetInteractable?.solved == true) {
            codeFeedbackLabel.text = "Correct"
            codeFeedbackLabel.foreground = Color.green
        } else {
            codeFeedbackLabel.text = "Incorrect"
            codeFeedbackLabel.foreground = Color.red
        }
    }

    fun show(interactable: Interactable) {
        targetInteractable = interactable
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