function toggleRaceDropdown() {
    const checkbox = document.getElementById("isRace");
    const dropdown = document.getElementById("race");
    const hiddenIdRace = document.getElementById("hiddenIdRace");

    if (checkbox.checked) {
        dropdown.disabled = false; 
        hiddenIdRace.disabled = true; 
    } else {
        dropdown.disabled = true;
        hiddenIdRace.disabled = false;
    }
}