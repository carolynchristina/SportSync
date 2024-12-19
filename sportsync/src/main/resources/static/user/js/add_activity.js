function toggleRaceDropdown() {
    const checkbox = document.getElementById("is_race");
    const dropdown = document.getElementById("race");
    dropdown.attributes.disabled = !checkbox.checked;
}
