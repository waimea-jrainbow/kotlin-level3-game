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
                                   ==============================
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
                                   ============================
                                   A wide, low-ceilinged room lined with magnetic clamping racks. Crates of
                                   various sizes are locked to the walls and floor in neat rows, each labeled
                                   with a stenciled symbol rather than text — station protocol, apparently.
                                   A manifest terminal on the near wall flickers with corrupted data. A tool
                                   locker stands open in the corner, mostly stripped bare. The room smells
                                   faintly of machine oil and something burnt.</html>"""
            )
            rooms.add(cell)
            rooms.add(guardRoom)

            cell.addExit(guardRoom)

            guardRoom.addExit(cell)
            guardRoom.addExit(cargoBay)

            cargoBay.addExit(guardRoom)


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
        private val exit2Button =JButton()


//    private val infoWindow = InfoWindow(this, app)      // Pass app state to dialog too

        init {
            setupLayout()
            setupStyles()
            setupActions()
            setupWindow()
            updateUI()
        }

        private fun setupLayout() {
            panel.preferredSize = java.awt.Dimension(450, 350)


            currentRoomDescLabel.setBounds(30, 80, 300, 160)
            exit1Button.setBounds(30,250,100,50)
            exit2Button.setBounds(80,250,100,50)


            panel.add(currentRoomDescLabel)
            panel.add(exit1Button)
            panel.add(exit2Button)


        }

        private fun setupStyles() {

            currentRoomDescLabel.font = Font(Font.SANS_SERIF, Font.BOLD, 12)

            exit1Button.font = Font(Font.SANS_SERIF, Font.BOLD, 8)
            exit1Button.font = Font(Font.SANS_SERIF, Font.BOLD, 8)


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


        private fun updateUI() {
            currentRoomDescLabel.text = app.currentRoom.description
            exit1Button.text = app.currentRoom.exits[0].name
            exit2Button.text = if (app.currentRoom.exits.size > 1) app.currentRoom.exits[1].name else ""
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