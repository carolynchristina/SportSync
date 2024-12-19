function toggleRaceDropdown() {
    const checkbox = document.getElementById("isRace");
    const dropdown = document.getElementById("race");
    dropdown.disabled = !checkbox.checked;
}
