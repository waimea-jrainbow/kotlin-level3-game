# Plan for Testing the Program

The test plan lays out the actions and data I will use to test the functionality of my program.

Terminology:

- **VALID** data values are those that the program expects
- **BOUNDARY** data values are at the limits of the valid range
- **INVALID** data values are those that the program should reject

---

## Examine items in a room

Click on an item in the examine list in the right-hand panel to verify that the correct 
description is displayed for that item in the description area below the room buttons.

### Test Data To Use

Select the "Bunk" interactable in Cell 01. This is a standard non-puzzle interactable 
with a simple description, making it a straightforward valid test case for the examine functionality.

### Expected Test Result

The description area should update to display the Bunk's description about the graffiti and the carved 
message "THE STARS DON'T LIE — 3 BRIGHT, 1 DIM, 2 GONE." No puzzle window should open and no inventory change should occur.

---

## Move between rooms

Click an exit button to move from one room to an adjacent connected room and verify that the room description, 
examine list, and exit buttons all update correctly.

### Test Data To Use

From Cell 01, click the "GUARD STATION" exit button. This is valid data as the Guard Station is a defined exit 
of Cell 01 and the cell has no unsolved puzzles blocking movement initially... actually the Panel puzzle blocks movement, 
so first solve the panel puzzle, then attempt to move. This tests normal valid room traversal.

### Expected Test Result

The current room description should change to the Guard Station description, the examine list should populate with the 
Guard Station's interactables (Console, Star map, Photo, Desk, Doors), and the exit buttons should update to reflect the Guard Station's exits.

---

## Move to room when unable to - INVALID

Attempt to click an exit button while there is an unsolved puzzle interactable in the current room, to verify that the movement is blocked.

### Test Data To Use

In Cell 01, without having solved the Panel puzzle, attempt to click the exit button leading to the Guard Station. This is invalid data 
in context because the puzzle gating condition has not been met.

### Expected Test Result

The exit button to the Guard Station should not be visible at all, as the updateUI() function hides exit buttons when canMove 
is false and the destination has not been previously visited. The player should remain in Cell 01.

---

## complete first puzzle - VALID

Open the Panel puzzle in Cell 01 and enter the correct three-digit code to verify that the puzzle is marked as solved and movement is unlocked.

### Test Data To Use

The correct code is "312", derived from the clue on the Bunk ("3 BRIGHT, 1 DIM, 2 GONE") matching the vent lights. Enter digits 3, 1, 2 in 
sequence and press OK

### Expected Test Result

The feedback label should display "Correct" in green, the dialog should close after approximately 900ms, the Panel interactable's solved 
property should be set to true, and the exit button to the Guard Station should become visible.

---

## Get first puzzle incorrect - INVALID

Example test description. Example test description. Example test description. Example test description. Example test description. Example test description.

### Test Data To Use

Details of test data and reasons for selection. Details of test data and reasons for selection. Details of test data and reasons for selection.

### Expected Test Result

Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen.

---

## get second puzzle incorrect and then correct

Example test description. Example test description. Example test description. Example test description. Example test description. Example test description.

### Test Data To Use

Details of test data and reasons for selection. Details of test data and reasons for selection. Details of test data and reasons for selection.

### Expected Test Result

Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen.

---

## Collect ID card from desk in second room

Example test description. Example test description. Example test description. Example test description. Example test description. Example test description.

### Test Data To Use

Details of test data and reasons for selection. Details of test data and reasons for selection. Details of test data and reasons for selection.

### Expected Test Result

Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen.

---

## Collect items from cargo bay

Example test description. Example test description. Example test description. Example test description. Example test description. Example test description.

### Test Data To Use

Details of test data and reasons for selection. Details of test data and reasons for selection. Details of test data and reasons for selection.

### Expected Test Result

Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen.

---

## get reactor puzzle incorrect and then correct

Example test description. Example test description. Example test description. Example test description. Example test description. Example test description.

### Test Data To Use

Details of test data and reasons for selection. Details of test data and reasons for selection. Details of test data and reasons for selection.

### Expected Test Result

Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen.

---

## Attempt override puzzle with no items

Example test description. Example test description. Example test description. Example test description. Example test description. Example test description.

### Test Data To Use

Details of test data and reasons for selection. Details of test data and reasons for selection. Details of test data and reasons for selection.

### Expected Test Result

Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen.

---

## Attempt override puzzle with laser cutter only

Example test description. Example test description. Example test description. Example test description. Example test description. Example test description.

### Test Data To Use

Details of test data and reasons for selection. Details of test data and reasons for selection. Details of test data and reasons for selection.

### Expected Test Result

Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen.

---

## Attempt override puzzle with ID card only

Example test description. Example test description. Example test description. Example test description. Example test description. Example test description.

### Test Data To Use

Details of test data and reasons for selection. Details of test data and reasons for selection. Details of test data and reasons for selection.

### Expected Test Result

Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen.

---

## Attempt override puzzle with both items

Example test description. Example test description. Example test description. Example test description. Example test description. Example test description.

### Test Data To Use

Details of test data and reasons for selection. Details of test data and reasons for selection. Details of test data and reasons for selection.

### Expected Test Result

Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen.

---

## Escape and complete the game

Example test description. Example test description. Example test description. Example test description. Example test description. Example test description.

### Test Data To Use

Details of test data and reasons for selection. Details of test data and reasons for selection. Details of test data and reasons for selection.

### Expected Test Result

Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen. Statement detailing what should happen.

---

