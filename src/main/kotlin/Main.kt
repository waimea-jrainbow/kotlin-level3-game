import com.formdev.flatlaf.themes.FlatMacDarkLaf
import java.awt.Font
import javax.swing.*

class Object(
    private val name: String,
    private val description: Int,
    private var examined:Boolean = false
){
    fun examine(){
        println("You examine the $name")
        examined = true
    }
}

class Room(
    val name: String,
    val description: String,

) {
    val exits: MutableList<Room> = mutableListOf()

    fun addExit(exit: Room) {
        exits.add(exit)

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

        init {
            setUpMap()
            currentRoom = rooms[0]
            println(currentRoom.name)
        }

        fun setUpMap() {
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
        val frame = JFrame("WINDOW TITLE")
        private val panel = JPanel().apply { layout = null }

        private val currentRoomDescLabel = JLabel(app.currentRoom.description)
        private val exit1Button = JButton()
        private val exit2Button = JButton()
        private val exit3Button = JButton()
        private val exit4Button = JButton()


//    private val infoWindow = InfoWindow(this, app)      // Pass app state to dialog too

        init {
            setupLayout()
            setupStyles()
            setupActions()
            setupWindow()
            updateUI()
        }

        private fun setupLayout() {
            panel.preferredSize = java.awt.Dimension(500, 500)


            currentRoomDescLabel.setBounds(30, 10, 300, 300)
            exit1Button.setBounds(30,350,100,50)
            exit2Button.setBounds(150,350,100,50)
            exit3Button.setBounds(270,350,100,50)
            exit4Button.setBounds(390,350,100,50)


            panel.add(currentRoomDescLabel)
            panel.add(exit1Button)
            panel.add(exit2Button)
            panel.add(exit3Button)
            panel.add(exit4Button)


        }

        private fun setupStyles() {

            currentRoomDescLabel.font = Font(Font.SANS_SERIF, Font.BOLD, 12)

            exit1Button.font = Font(Font.SANS_SERIF, Font.BOLD, 8)
            exit2Button.font = Font(Font.SANS_SERIF, Font.BOLD, 8)
            exit3Button.font = Font(Font.SANS_SERIF, Font.BOLD, 8)
            exit4Button.font = Font(Font.SANS_SERIF, Font.BOLD, 8)


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


        private fun updateUI() {
            currentRoomDescLabel.text = app.currentRoom.description
            exit1Button.text = app.currentRoom.exits[0].name
            exit2Button.text = if (app.currentRoom.exits.size > 1) app.currentRoom.exits[1].name else ""
            exit3Button.text = if (app.currentRoom.exits.size > 2) app.currentRoom.exits[2].name else ""
            exit4Button.text = if (app.currentRoom.exits.size > 3) app.currentRoom.exits[3].name else ""

            exit2Button.isVisible = app.currentRoom.exits.size > 1
            exit3Button.isVisible = app.currentRoom.exits.size > 2
            exit4Button.isVisible = app.currentRoom.exits.size > 3

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
//class InfoWindow(val owner: MainWindow, val app: App) {
//    private val dialog = JDialog(owner.frame, "DIALOG TITLE", false)
//    private val panel = JPanel().apply { layout = null }
//
//    private val infoLabel = JLabel()
//    private val resetButton = JButton("Reset")
//
//    init {
//        setupLayout()
//        setupStyles()
//        setupActions()
//        setupWindow()
//        updateUI()
//    }
//
//    private fun setupLayout() {
//        panel.preferredSize = java.awt.Dimension(240, 180)
//
//        infoLabel.setBounds(30, 30, 180, 60)
//        resetButton.setBounds(30, 120, 180, 30)
//
//        panel.add(infoLabel)
//        panel.add(resetButton)
//    }
//
//    private fun setupStyles() {
//        infoLabel.font = Font(Font.SANS_SERIF, Font.PLAIN, 16)
//        resetButton.font = Font(Font.SANS_SERIF, Font.PLAIN, 16)
//    }
//
//    private fun setupWindow() {
//        dialog.isResizable = false                              // Can't resize
//        dialog.defaultCloseOperation = JDialog.HIDE_ON_CLOSE    // Hide upon window close
//        dialog.contentPane = panel                              // Main content panel
//        dialog.pack()
//    }
//
////    private fun setupActions() {
////        resetButton.addActionListener { handleResetClick() }
////    }
////
////    private fun handleResetClick() {
////        app.resetScore()    // Update the app state
////        owner.updateUI()    // Update the UI to reflect this, via the main window
////    }
////
////    fun updateUI() {
////        // Use app properties to display state
////        infoLabel.text = "<html>User: ${app.name}<br>Score: ${app.score} points"
////
////        resetButton.isEnabled = app.score > 0
////    }
//
//    fun show() {
//        val ownerBounds = owner.frame.bounds          // get location of the main window
//        dialog.setLocation(                           // Position next to main window
//            ownerBounds.x + ownerBounds.width + 10,
//            ownerBounds.y
//        )
//
//        dialog.isVisible = true
//    }
//}