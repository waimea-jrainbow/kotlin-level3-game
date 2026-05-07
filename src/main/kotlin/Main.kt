import com.formdev.flatlaf.themes.FlatMacDarkLaf
import java.awt.Color
import java.awt.Font
import javax.swing.*


/**
 * Represents an object within a Room that the player can examine or interact with.
 *
 * Interactable may be an object with descriptions (e.g. a bunk or a door) or a puzzle
 * object that must be solved before the player can progress. Each interactable tracks
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
    var solved = false

    override fun toString() = name // When toString() is used on an interactable its name will be returned
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
 * @property visited       The boolean that tracks whether this room has been visited before
 */
class Room(
    val name: String,
    val description: String,
    var interactables: MutableList<Interactable> = mutableListOf(),
    var visited: Boolean = false


) {
    // Making the exits list
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
 * @property inventory            The mutable list of empty inventory slots that get filled as items are
 *                                collected
 */
class App {
    var currentRoom: Room
    private val rooms: MutableList<Room> = mutableListOf()
    var currentInteractable: Interactable? = null
    var canMove = true
    var inventory: MutableList<String> = mutableListOf("[_______]", "[______]", "[____________]")

    /**
     * Initializes the game by running all setup functions and setting the current room to thr room in the
     * first index of the rooms list
     */
    init {
        setUpRooms()
        setUpInteractables(rooms)
        currentRoom = rooms[0]
        println(currentRoom.name)

    }

    /**
     * Function that sets up all the rooms with their names and descriptions
     * and then adds them to the rooms list.
     *
     * Adds rooms to the exits list of each room to allow movement between them
     */
    private fun setUpRooms() {
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
                                   A large computer monitor flashes a warning symbol casting more red light into the room.
                                   <br>
                                   There is a placard on the wall beside the levers.
                                   """
        )


        val airLock = Room(
            "AIR LOCK",
            """<html><wrap>
                                   AIRLOCK — DOCKING RING 3
                                   ========================= <br>
                                   A heavy pressure door dominates the far wall, its surface scratched and
                                   dented from years of use. Beside it: a biometric scanner with a smashed screen, dark and
                                   inert. On the near wall, a manual override panel sits behind a sealed
                                   metal plate, its edges fused, someone welded it shut deliberately.
                                   There's a large sign on the other side of the door.
                                   A small porthole to the right shows open space: stars, and the dim
                                   shape of a drifting salvage shuttle about two hundred meters out.

                                   The scanner is dark. The override panel is inaccessible.
                                   You're close. Not there yet.</html>"""
        )

        val exit = Room(
            "Salvage shuttle, escape", """<html><wrap>The airlock door slowly grinds open and you step in. The door closes behind you and another opens and you climb inside of the salvage shuttle. 
                   you power up its engines and make your escape.
                   
                   Congratulations you escaped!!!
            """)

        //Add all the rooms to the rooms list
        rooms.add(cell)
        rooms.add(guardRoom)
        rooms.add(cargoBay)
        rooms.add(reactorRoom)
        rooms.add(airLock)
        rooms.add(exit)

        //Add the exits to each rooms exits list
        cell.addExit(guardRoom)

        guardRoom.addExit(cell)
        guardRoom.addExit(cargoBay)
        guardRoom.addExit(reactorRoom)

        cargoBay.addExit(guardRoom)

        reactorRoom.addExit(guardRoom)
        reactorRoom.addExit(airLock)

        airLock.addExit(reactorRoom)
        airLock.addExit(exit)



    }

    /**
     * Function that sets up all the interactables with their names, descriptions and if they are a puzzle or not
     * and then adds them to the relevant rooms interactables list.
     *
     * @param rooms The mutable list of rooms which contains all rooms in the map
     */
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
                                                               bottom drawer slides open inside is a guard ID card. You take it. You don't think the 
                                                               guard will be needing it anytime soon"""
        )

        val photo = Interactable("Photo","""<html><wrap>You flip it over. A guard in dress uniform, smiling with a family.
                                                                You set it back down, face-up this time.""")

        val doors = Interactable("Doors", """<html><wrap>Three reinforced security doors. Door A is marked REACTOR - AUTHORIZED
                                                                             PERSONNEL ONLY. Door B is marked MEDICAL. Both are sealed. The corridor
                                                                             to the cargo bay has no door just an open arch.""")

        rooms[1].addInteractable(console)
        rooms[1].addInteractable(starMap)
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

        val crate1 = Interactable("Crate 1","""<html><wrap>An open crate containing sealed ration packs. You aren't interested in food right now
                                                                         and besides you have no way to heat them up.""")

        val crate2 = Interactable("Crate 2","""<html><wrap>An open crate containing medical supplies. You decide to take a medkit. Just in case""")

        val crate3 = Interactable("Crate 3", """<html><wrap>An open crate containing a laser cutter. You decide to take it. Just in case.""")

        val crate4 = Interactable("Crate 4","""<html><wrap>A sealed crate labelled with advertising for Meat substitute™. It sounds disgusting. Luckily 
                                                                          you can't get in.""")

        val crate5 = Interactable("Crate 5", """<html><wrap>A sealed crate. According to the manifest it contains a life raft. Why you need a life raft in space
                                                                            and at this point you can't be bothered to find out.""")

        val crate6 = Interactable("Crate 6", """<html><wrap>An open crate. It contains canisters of Cool Coolant™. You decide to take a canister.
                                                                            Just in case.""")


        rooms[2].addInteractable(manifest)
        rooms[2].addInteractable(crate1)
        rooms[2].addInteractable(crate2)
        rooms[2].addInteractable(crate3)
        rooms[2].addInteractable(crate4)
        rooms[2].addInteractable(crate5)
        rooms[2].addInteractable(crate6)

        // Reactor setup

        val reactorCasing = Interactable("Reactor Casing", """<html><wrap>A dark metal shell covered in scorch marks. It's vibrating angrily.""")

        val placard = Interactable("Placard", """<html><wrap>!! PLASMA VENT PROTOCOL !!<br>
                                                                                ONLY QUALIFIED TECHNICIANS WIELDING COOL COOLANT™ ARE TO ATTEMPT<br>
                                                                                NEVER OPEN INTAKE AND EXHAUST SIMULTANEOUSLY.<br>
                                                                                ALWAYS CLOSE EXHAUST BEFORE INTAKE.<br>
                                                                                FAILURE TO COMPLY WILL RESULT IN CORE OVERLOAD.""")

        val computer = Interactable("Computer","""<html><wrap>You try to use the computers keyboard. A loud error sound blasts out of the speakers making you jump back.
                                                                              You take a few more steps away from the computer and eye it suspiciously.""")

        val levers = Interactable("Exhaust & intake levers","""<html><wrap>A pair of large metal levers which will close the exhaust and intake pipes leading in and out of the reactor core.""",true)


        rooms[3].addInteractable(reactorCasing)
        rooms[3].addInteractable(placard)
        rooms[3].addInteractable(computer)
        rooms[3].addInteractable(levers)

        val airlockDoor = Interactable("Airlock door", """<html><wrap>blep""")

        val biometricScanner = Interactable("Biometric scanner", """<html><wrap>You tap the screen, it blazes to life for all of about a second 
                                                                                     before fizzling out and producing a few sparks.""")

        val overridePanel = Interactable("Override panel", """<html><wrap>A panel labelled OVERRIDE. 
                                                                                              The edges are fused shut — someone welded this deliberately. 
                                                                                              A laser cutter could get through that.""", true)

        rooms[4].addInteractable(airlockDoor)
        rooms[4].addInteractable(biometricScanner)
        rooms[4].addInteractable(overridePanel)
    }


    /**
     * Function to check whether the current interactable has been solved
     *
     * @return Whether the interactable has been solved
     */
    fun currentInteractableSolved(): Boolean {
        return currentInteractable?.solved ?: false
    }

    /**
     *  Function to move the player to a new room
     *
     *  @param newRoom the new room the player is moving too
     */
    fun moveRoom(newRoom: Room) {
        currentRoom.visited = true
        currentRoom = newRoom
    }

}


/**
 * The primary application window shows the current room, its exits and its examinable objects
 *
 * @param app The shared App state object that this window reads from and writes to.
 */
class MainWindow(private val app: App) {
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

    private val inventoryListLabel = JLabel("Inventory:")
    private val inventoryList = JLabel()

    private val puzzle1Window = PuzzleWindow(this, app, "312")
    private val puzzle2Window = PuzzleWindow(this, app, "365")

    private val puzzleWindowReactor = PuzzleWindowReactor(this, app)

    private val puzzleWindowOverride = PuzzleWindowOverride(this, app)

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

    /**
     * Set up the layout of all elements in the window and then add them to the panel
     */
    private fun setupLayout() {
        panel.preferredSize = java.awt.Dimension(650, 700)

        currentRoomDescLabel.setBounds(30, 0, 300, 300)
        exit1Button.setBounds(30, 350, 100, 50)
        exit2Button.setBounds(150, 350, 100, 50)
        exit3Button.setBounds(270, 350, 100, 50)
        exit4Button.setBounds(390, 350, 100, 50)
        examineListLabel.setBounds(350, 5, 150, 30)
        examineList.setBounds(350, 35, 150, 290)
        examinedInteractableDescLabel.setBounds(30, 420, 450, 160)
        inventoryListLabel.setBounds(550,5,150,30)
        inventoryList.setBounds(550, 25, 150, 60)


        panel.add(currentRoomDescLabel)
        panel.add(exit1Button)
        panel.add(exit2Button)
        panel.add(exit3Button)
        panel.add(exit4Button)
        panel.add(examineListLabel)
        panel.add(examineList)
        panel.add(examinedInteractableDescLabel)
        panel.add(inventoryListLabel)
        panel.add(inventoryList)


    }

    /**
     * Set up styling for all elements that need it
     */
    private fun setupStyles() {

        currentRoomDescLabel.font = Font(Font.SANS_SERIF, Font.BOLD, 12)

        exit1Button.font = Font(Font.SANS_SERIF, Font.BOLD, 8)
        exit2Button.font = Font(Font.SANS_SERIF, Font.BOLD, 8)
        exit3Button.font = Font(Font.SANS_SERIF, Font.BOLD, 8)
        exit4Button.font = Font(Font.SANS_SERIF, Font.BOLD, 8)
        examineListLabel.font = Font(Font.SANS_SERIF, Font.BOLD, 12)
        examineList.font = Font(Font.SANS_SERIF, Font.BOLD, 12)
        inventoryListLabel.font = Font(Font.SANS_SERIF, Font.BOLD, 12)
        inventoryList.font = Font(Font.SANS_SERIF, Font.BOLD, 12)
    }

    private fun setupWindow() {
        frame.isResizable = true                         // Can't resize
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE  // Exit upon window close
        frame.contentPane = panel                           // Define the main content
        frame.pack()                                        // Size the window based on components
        frame.setLocationRelativeTo(null)                   // Centre on the screen
    }

    /**
     * Set up the actions of buttons and other similar elements
     */
    private fun setupActions() {
        exit1Button.addActionListener { handleExit1Click() }
        exit2Button.addActionListener { handleExit2Click() }
        exit3Button.addActionListener { handleExit3Click() }

        examineList.addListSelectionListener { event -> // Check for a change in selection in the list
            if (!event.valueIsAdjusting) { // Ensure the listener only fires when the selection is changed
                val selected = examineList.selectedValue // Compare the selected value to the list of interactables for the room
                if (selected != null) {
                    examinedInteractableDescLabel.text = selected.description // Change the description currently displayed to the selected interactable

                    app.currentInteractable = selected // Change the current interactable to the selected one

                    // Check whether the interactable has an associated action
                    when (selected.name) {
                        "Panel" -> puzzle1Window.show(selected) // Show the puzzle dialog

                        "Console" -> puzzle2Window.show(selected)

                        "Desk" -> app.inventory[0] = "[ID card]" // Add the relevant item to the inventory

                        "Crate 2" -> app.inventory[1] = "[Medkit]"

                        "Crate 3" -> app.inventory[2] = "[Laser cutter]"

                        "Exhaust & intake levers" -> puzzleWindowReactor.show(selected)

                        "Override panel" -> puzzleWindowOverride.show(selected)



                    }
                    updateUI()
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
        app.moveRoom(app.currentRoom.exits[1])
        updateUI()
        println(app.currentRoom.name)
    }

    private fun handleExit3Click() {
        app.moveRoom(app.currentRoom.exits[2])
        updateUI()
        println(app.currentRoom.name)
    }



    fun updateUI() {
        app.canMove = true
        val exits = app.currentRoom.exits // Get the current rooms exits

        // Check if all puzzles in the current room are solved, if not then set canMove to false
        app.currentRoom.interactables.forEach { interactable ->
            if (interactable.isPuzzle && !interactable.solved)
                app.canMove = false
        }

        currentRoomDescLabel.text = app.currentRoom.description
        exit1Button.text = if (app.currentRoom.exits.isNotEmpty()) app.currentRoom.exits[0].name else "" // Check if there is any exit to the current room available, if there is set the label to the exit name
        exit2Button.text = if (app.currentRoom.exits.size > 1) app.currentRoom.exits[1].name else "" // Check if there is more than 1 exit, if there is set the label to the exit name
        exit3Button.text = if (app.currentRoom.exits.size > 2) app.currentRoom.exits[2].name else "" //  Check if there is more than 2 exits, if there is set the label to the exit name


        exit1Button.isVisible = exits.isNotEmpty() && (app.canMove || exits[0].visited) // If there is an exit and the player can move or the previous room has already been visited make the button visible
        exit2Button.isVisible = exits.size > 1 && (app.canMove || exits[1].visited) // If there is more than 1 exit and the player can move or the previous room has already been visited make the button visible
        exit3Button.isVisible = exits.size > 2 && (app.canMove || exits[2].visited) // If there is more than 2 exits and the player can move or the previous room has already been visited make the button visible

        examineListModel.clear() // Clear the examine list
        app.currentRoom.interactables.forEach { interactable ->
            examineListModel.addElement(interactable) // Fill the examine list
        }

        inventoryList.text = "<html>" + app.inventory.joinToString("<br>") + "</html>"

        println(app.inventory.joinToString(", "))


    }

    fun show() {
        frame.isVisible = true
    }
}


/**
 * A JDialog that presents the maintenance-panel number puzzle to the player.
 *
 * The puzzle displays a reactor interface with 2 buttons to close 2 valves
 * when the correct order of valves is closed the dialog closes automatically
 * after a short delay and marks the associated Interactable as solved via
 * App.interactableSolved, which then allows the player to move the next room
 *
 * @param owner The MainWindow that owns this dialog, used for positioning.
 * @param app   The shared App state object used to check and update puzzle state.
 */
class PuzzleWindowReactor(private val owner: MainWindow, private val app: App) {
    private val dialog = JDialog(owner.frame, "Close the levers", true)
    private val panel = JPanel().apply { layout = null }

    private var targetInteractable: Interactable? = null
    private val reactorGraphic = JLabel("""<html><wrap>==================================<br>
                                                              ====| I |==| REACTOR |==| E |====<br>
                                                              =================================<br>""")

    private val leverFeedbackLabel1 = JLabel("""<html><wrap>INTAKE<br>
                                                                  [OPEN]""")
    private val leverFeedbackLabel2 = JLabel("""<html><wrap>EXHAUST<br>
                                                                   [OPEN]""")
    private val buttonIntake = JButton("I")
    private val buttonExhaust = JButton("E")
    private val buttonReset = JButton("Reset")
    private var enteredCode = mutableListOf<Int>()
    private val reactorStatus = JLabel("Unsafe")

    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
    }

    private fun setupLayout() {
        panel.preferredSize = java.awt.Dimension(240, 320)

        reactorGraphic.setBounds(20, 40, 200, 80)

        leverFeedbackLabel1.setBounds(60, 100, 200, 80)
        leverFeedbackLabel2.setBounds(120, 100, 200, 80)


        buttonIntake.setBounds(50,   180, 60, 60)
        buttonExhaust.setBounds(120,  180, 60, 60)

        buttonReset.setBounds(0,   245, 100, 60)

        reactorStatus.setBounds(100,80,60,60)



        panel.add(reactorGraphic)
        panel.add(leverFeedbackLabel1)
        panel.add(leverFeedbackLabel2)
        panel.add(buttonIntake)
        panel.add(buttonExhaust)
        panel.add(buttonReset)
        panel.add(reactorStatus)
    }

    private fun setupStyles() {
        reactorGraphic.font = Font(Font.DIALOG_INPUT, Font.BOLD, 10)

        reactorStatus.foreground =  Color.red

    }

    private fun setupWindow() {
        dialog.isResizable = true                              // Can't resize
        dialog.defaultCloseOperation = JDialog.HIDE_ON_CLOSE    // Hide upon window close
        dialog.contentPane = panel                              // Main content panel
        dialog.pack()
    }

    private fun setupActions() {
        buttonIntake.addActionListener { handleLeverClick(1) }
        buttonExhaust.addActionListener { handleLeverClick(2) }
        buttonReset.addActionListener { handleClrClick() }
    }



    private fun handleLeverClick(lever:Int) {
        enteredCode.add(lever)
        leverStatusText(lever)
        println(enteredCode.toString())
        if (enteredCode.size == 2) checkLevers()
        updateUI()
    }

    /**
     * Check whether the levers were flipped in the right order and handle what happens if it is correct
     */
    private fun checkLevers() {
        println(enteredCode.toString())
        if (enteredCode.joinToString("") == "21") {
            targetInteractable?.solved = true
            updateUI()

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
            val closeTimer = Timer(900) {
                enteredCode.clear()
                updateUI()
            }
            closeTimer.isRepeats = false
            closeTimer.start()
        }

    }


    private fun leverStatusText(state: Int) {
        when (state) {
            1 -> leverFeedbackLabel1.text = """<html><wrap>INTAKE<br>
                                                                [CLOSED]"""

            2 -> leverFeedbackLabel2.text = """<html><wrap>EXHAUST<br>
                                                                [CLOSED]"""
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
        if (targetInteractable?.solved == true) {
            reactorStatus.text = "Safe"
            reactorStatus.foreground = Color.green

        }
        else {
            reactorStatus.text = "Unsafe"
            reactorStatus.foreground = Color.red

            if (enteredCode.isEmpty()) {
                leverFeedbackLabel1.text = """<html><wrap>INTAKE<br>
                                              [OPEN]"""
                leverFeedbackLabel2.text = """<html><wrap>EXHAUST<br>
                                              [OPEN]"""
            }

        }
    }

    private fun handleClrClick() {
        enteredCode.clear()
        updateUI()
    }


}

/**
 * A JDialog that presents a panel number puzzle to the player.
 *
 * The puzzle displays a nine-digit code interface with buttons for the digits 1–9,
 * a clear button, and a confirm button. On correct entry the dialog closes automatically
 * after a short delay and marks the associated
 * Interactable as solved via App.interactableSolved, which then allows the player to move
 * the next room
 *
 * @param owner The MainWindow that owns this dialog, used for positioning.
 * @param app   The shared App state object used to check and update puzzle state.
 * @param code The correct code for the puzzle
 */
class PuzzleWindow(private val owner: MainWindow, private val app: App, private val code:String) {
    private val dialog = JDialog(owner.frame, "Enter code", true)
    private val panel = JPanel().apply { layout = null }

    private var targetInteractable: Interactable? = null

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

    /**
     * Function to handle input of a number
     */
    private fun handleNumClick(number: Int) {
        if (enteredCode.size < code.length) {
            enteredCode.add(number)
            println(enteredCode.toString())
            updateUI()
        }
    }

    /**
     * Function to check if the entered code is correct
     */
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
            // start a timer that only fires once to trigger the closing of the window
            closeTimer.isRepeats = false
            closeTimer.start()
            owner.updateUI()
        } else {
            codeStatusText()
            val statusClearTimer = Timer(1000) {
                enteredCode.clear()
                codeFeedbackLabel.text = ""
                updateUI()
            }
            // start a timer that only fires once to trigger removal if "Incorrect" label
            statusClearTimer.isRepeats = false
            statusClearTimer.start()
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

        // Shows the currently entered code seperated by - and empty spaces as _
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

/**
 * A JDialog that presents the override panel puzzle to the player.
 *
 * The puzzle displays a description of the faced obstacle and asks the user if they have the right item for the job
 * if they don't present the next obstacle. Once puzzle is solved marks the associated Interactable as solved via
 * App.interactableSolved, which then allows the player to go through the exit and finish the game
 *
 * @param owner The MainWindow that owns this dialog, used for positioning.
 * @param app   The shared App state object used to check and update puzzle state.
 */
class PuzzleWindowOverride(private val owner: MainWindow, private val app: App) {
    private val dialog = JDialog(owner.frame, "Override Panel", true)
    private val panel = JPanel().apply { layout = null }

    private var targetInteractable: Interactable? = null

    private val statusLabel = JLabel("")
    private val actionButton = JButton()
    private val feedbackLabel = JLabel("")

    // Tracks progress: 0 = nothing done, 1 = cut open, 2 = solved
    private var stage = 0

    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
    }

    private fun setupLayout() {
        panel.preferredSize = java.awt.Dimension(280, 200)

        statusLabel.setBounds(20, 10, 240, 80)
        actionButton.setBounds(20, 100, 240, 50)
        feedbackLabel.setBounds(20, 155, 240, 30)

        panel.add(statusLabel)
        panel.add(actionButton)
        panel.add(feedbackLabel)
    }

    private fun setupStyles() {
        statusLabel.font = Font(Font.DIALOG_INPUT, Font.PLAIN, 12)
        actionButton.font = Font(Font.SANS_SERIF, Font.BOLD, 12)
        feedbackLabel.font = Font(Font.DIALOG_INPUT, Font.PLAIN, 12)
    }

    private fun setupWindow() {
        dialog.isResizable = false
        dialog.defaultCloseOperation = JDialog.HIDE_ON_CLOSE
        dialog.contentPane = panel
        dialog.pack()
    }

    private fun setupActions() {
        actionButton.addActionListener { handleActionClick() }
    }

    private fun handleActionClick() {
        when (stage) {
            0 -> { // Need laser cutter to cut the panel open
                if (app.inventory.contains("[Laser cutter]")) {
                    stage = 1
                    feedbackLabel.text = "Panel cut open."
                    feedbackLabel.foreground = Color.green
                } else {
                    feedbackLabel.text = "You need something to cut with."
                    feedbackLabel.foreground = Color.red
                }
            }
            1 -> { // Need ID card to activate the terminal
                if (app.inventory.contains("[ID card]")) {
                    stage = 2
                    targetInteractable?.solved = true
                    feedbackLabel.text = "Access granted."
                    feedbackLabel.foreground = Color.green
                    val closeTimer = Timer(900) {
                        dialog.dispose()
                        owner.updateUI()
                    }
                    closeTimer.isRepeats = false
                    closeTimer.start()
                    owner.updateUI()
                } else {
                    feedbackLabel.text = "The terminal needs an ID card."
                    feedbackLabel.foreground = Color.red
                }
            }
        }
        updateUI()
    }

    fun show(interactable: Interactable) {
        targetInteractable = interactable
        feedbackLabel.text = "" // Clears feedback so it doesn't show up if the user quickly closes and open the dialog after getting it incorrect
        updateUI()

        val ownerBounds = owner.frame.bounds
        dialog.setLocation(
            ownerBounds.x + ownerBounds.width + 10,
            ownerBounds.y
        )

        if (!app.currentInteractableSolved()) dialog.isVisible = true
    }

    private fun updateUI() {
        when (stage) {
            0 -> {
                statusLabel.text = "<html>The panel edges are fused shut.<br>You'll need a laser cutter to get through.</html>"
                actionButton.text = "Cut panel open"
            }
            1 -> {
                statusLabel.text = "<html>The panel is open. A terminal blinks underneath.<br>It's waiting for an ID card.</html>"
                actionButton.text = "Use ID card"
            }
            2 -> {
                statusLabel.text = "<html>The override terminal is active.<br>The airlock unseals with a heavy clunk.</html>"
                actionButton.isEnabled = false
            }
        }
    }
}
