# Plan for Testing the Program

The test plan lays out the actions and data I will use to test the functionality of my program.

Terminology:

- **VALID** data values are those that the program expects
- **BOUNDARY** data values are at the limits of the valid range
- **INVALID** data values are those that the program should reject

---

## Examine items in a room - VALID

Click on an item in the examine list in the right-hand panel to verify that the correct description is displayed for that item in the description area below the room buttons.

### Test Data To Use

Select the "Bunk" interactable in Cell 01. This is a standard non-puzzle interactable with a simple description, making it a straightforward valid test case for the examine functionality.

### Expected Test Result

The description area should update to display the Bunk's description about the graffiti and the carved message "THE STARS DON'T LIE — 3 BRIGHT, 1 DIM, 2 GONE." No puzzle window should open and no inventory change should occur.

---

## Move between rooms - VALID

Click an exit button to move from one room to an adjacent connected room and verify that the room description, examine list, and exit buttons all update correctly.

### Test Data To Use

From Cell 01, first solve the Panel puzzle to unlock movement, then click the "GUARD STATION" exit button. This is valid data as the Guard Station is a defined exit of Cell 01 and the puzzle gating condition has been met.

### Expected Test Result

The current room description should change to the Guard Station description, the examine list should populate with the Guard Station's interactables (Console, Star map, Photo, Desk, Doors), and the exit buttons should update to reflect the Guard Station's exits.

---

## Move to room when unable to - INVALID

Attempt to click an exit button while there is an unsolved puzzle interactable in the current room, to verify that the movement is blocked.

### Test Data To Use

In Cell 01, without having solved the Panel puzzle, attempt to click the exit button leading to the Guard Station. This is invalid data in context because the puzzle gating condition has not been met.

### Expected Test Result

The exit button to the Guard Station should not be visible at all, as the updateUI() function hides exit buttons when canMove is false and the destination has not been previously visited. The player should remain in Cell 01.

---

## Complete first puzzle - VALID

Open the Panel puzzle in Cell 01 and enter the correct three-digit code to verify that the puzzle is marked as solved and movement is unlocked.

### Test Data To Use

The correct code is "312", derived from the clue on the Bunk ("3 BRIGHT, 1 DIM, 2 GONE") matching the vent lights. Enter digits 3, 1, 2 in sequence and press OK. This is valid data.

### Expected Test Result

The feedback label should display "Correct" in green, the dialog should close after approximately 900ms, the Panel interactable's solved property should be set to true, and the exit button to the Guard Station should now become visible.

---

## Get first puzzle incorrect - INVALID

Open the Panel puzzle in Cell 01 and enter a wrong code to verify that the incorrect entry is handled gracefully and the puzzle remains unsolved.

### Test Data To Use

Enter the code "111" and press OK. This is invalid data as it does not match the correct code of "312".

### Expected Test Result

The feedback label should display "Incorrect" in red, the entered code should clear after approximately 1000ms, the feedback label should then disappear, and the puzzle should remain unsolved with the exit button still hidden.

---

## Get second puzzle incorrect and then correct - INVALID and VALID

Open the Console puzzle in the Guard Station, first enter a wrong code, then enter the correct code, and verify the correct behaviour in both cases.

### Test Data To Use

First enter "111" as invalid data, then enter "365" as valid data. The correct code of 365 is derived from the star map clue showing three stars, six stars, five stars.

### Expected Test Result

On entering "111" the feedback label should show "Incorrect" in red and clear after 1000ms. On then entering "365" the feedback label should show "Correct" in green and the dialog should close after 900ms, marking the Console as solved.

---

## Collect ID card from desk in second room - VALID

Click the Desk interactable in the Guard Station and verify that the ID card is added to the player's inventory.

### Test Data To Use

Navigate to the Guard Station and select "Desk" from the examine list. This is valid data as the Desk is present in the room and the inventory has a slot pre-set to "[______]" waiting for it.

### Expected Test Result

The inventory display should update so that the second slot changes from "[______]" to "[ID card]", and the Desk description about the guard ID card should appear in the description area.

---

## Collect items from cargo bay - VALID

Click Crate 2 and Crate 3 in the Cargo Bay to verify that the medkit and laser cutter are added to the inventory correctly.

### Test Data To Use

Navigate to the Cargo Bay and select "Crate 2" then "Crate 3" from the examine list. These are valid interactions as both crates are open and their items are available.

### Expected Test Result

After selecting Crate 2 the inventory should show "[Medkit]" in the second slot. After selecting Crate 3 the inventory should show "[Laser cutter]" in the third slot. Both descriptions should display the appropriate flavour text.

---

## Get reactor puzzle incorrect and then correct - INVALID and VALID

Open the lever puzzle in the Reactor Room, press the levers in the wrong order first, then reset and press them in the correct order.

### Test Data To Use

First press Intake (I) then Exhaust (E), which is the incorrect order. Then press Reset and press Exhaust (E) then Intake (I), which is the correct order as specified by the placard ("ALWAYS CLOSE EXHAUST BEFORE INTAKE").

### Expected Test Result

The incorrect order should not mark the puzzle as solved and the reactor status label should remain red showing "Unsafe". After resetting and entering the correct order (Exhaust then Intake), the status label should change to "Safe" in green and the dialog should close after 900ms, marking the levers as solved.

---

## Attempt override puzzle with no items - INVALID

Open the Override Panel puzzle in the Airlock with no relevant items in the inventory and attempt to cut the panel open.

### Test Data To Use

Navigate to the Airlock without collecting the laser cutter or ID card and select "Override panel". Click the "Cut panel open" button. This is invalid data as the required item is absent.

### Expected Test Result

The feedback label should display "You need something to cut with." in red and the stage should remain at 0, leaving the panel unsolved and the exit to escape still blocked.

---

## Attempt override puzzle with laser cutter only - INVALID

Open the Override Panel puzzle with only the laser cutter in the inventory, cut the panel open, and then attempt to use the ID card terminal.

### Test Data To Use

Collect the laser cutter from Crate 3 but do not collect the ID card from the Desk. Open the override panel and click "Cut panel open" then "Use ID card". The first click is valid input, the second is invalid as the ID card is absent.

### Expected Test Result

Clicking "Cut panel open" should succeed, updating the status label to show the open terminal and advancing to stage 1. Clicking "Use ID card" should then display "The terminal needs an ID card." in red, leaving the puzzle unsolved.

---

## Attempt override puzzle with ID card only - INVALID

Open the Override Panel puzzle with only the ID card in the inventory and attempt to cut the panel without a laser cutter.

### Test Data To Use

Collect the ID card from the Desk but do not collect the laser cutter from Crate 3. Open the override panel and click "Cut panel open". This is invalid data as the laser cutter is absent.

### Expected Test Result

The feedback label should display "You need something to cut with." in red. The stage should remain at 0 and the puzzle should be unsolved, meaning the exit to escape remains blocked.

---

## Attempt override puzzle with both items - VALID

Open the Override Panel puzzle with both the laser cutter and the ID card collected and complete both stages.

### Test Data To Use

Collect the laser cutter from Crate 3 and the ID card from the Desk, then open the Override Panel in the Airlock. Click "Cut panel open" followed by "Use ID card". Both inputs are valid as the required items are present.

### Expected Test Result

Clicking "Cut panel open" should show "Panel cut open." in green and advance to stage 1. Clicking "Use ID card" should show "Access granted." in green, mark the Override Panel as solved, close the dialog after 900ms, and make the exit to the salvage shuttle visible.

---

## Escape and complete the game - VALID

With all puzzles solved and both items collected, click the exit from the Airlock to the salvage shuttle and verify the game completes.

### Test Data To Use

Having completed all prior puzzles and collected the ID card and laser cutter, click the exit button leading to the "Salvage shuttle, escape" room. This is valid data representing the successful completion state.

### Expected Test Result

The room description should update to display the escape victory text ("Congratulations you escaped!!!") and the player should be placed in the final exit room. No further exits should be shown as the exit room has no onward connections.

---

## Code entry - too few digits - BOUNDARY

Open a code entry puzzle and press OK after entering fewer digits than the required three, to verify that the puzzle correctly rejects an incomplete code.

### Test Data To Use

In Cell 01, open the Panel puzzle and enter only two digits, "3" and "1", then press OK without entering a third digit. This is boundary data as it is one step below the minimum valid input length of 3 digits.

### Expected Test Result

The entered code "3-1-_" should not match the correct code "312" and the feedback label should display "Incorrect" in red. The code should clear after approximately 1000ms and the puzzle should remain unsolved.

---

## Code entry - too many digits - BOUNDARY

Open a code entry puzzle and press OK after entering more digits than the required three, to verify that the puzzle correctly rejects an oversized code.

### Test Data To Use

In Cell 01, open the Panel puzzle and enter four digits, "3", "1", "2", "1", then press OK. This is boundary data as it is one step above the maximum valid input length of 3 digits.

### Expected Test Result

The entered code joined as a string will be "3121" which does not match "312", so the feedback label should display "Incorrect" in red. The code should clear after approximately 1000ms and the puzzle should remain unsolved.

---

## Lever sequence - too few presses - BOUNDARY

Open the lever puzzle in the Reactor Room and attempt to trigger a result by pressing only one lever, to verify that the puzzle does not check or accept an incomplete sequence.

### Test Data To Use

Open the Exhaust & intake levers puzzle and press only the Exhaust (E) button without pressing a second lever. This is boundary data as it is one press below the minimum valid sequence length of 2.

### Expected Test Result

The checkLevers() function should not fire at all as it only triggers when enteredCode.size == 2. No solved state should be set, the reactor status label should remain red showing "Unsafe", and the dialog should stay open waiting for a second input.

---