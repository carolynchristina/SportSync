document.addEventListener("DOMContentLoaded", function () {
    const checkbox = document.getElementById("isRace");
    const dropdown = document.getElementById("race");
    const distanceInput = document.getElementById("jarakTempuh");
    const hiddenIdRace = document.getElementById("hiddenIdRace");
    const submitButton = document.querySelector(".create-btn");    
    const distErrorMessage = document.querySelector("#distanceError");

    let currentMinDistance = 0;

    //validasi input distance jika suatu race diselect
    //return true jika (input jarakTempuh) >= (jarakTempuh race)
    function validateDistance() {
        if (checkbox.checked) {
            const enteredDistance = parseInt(distanceInput.value || "0", 10);

            if (enteredDistance < currentMinDistance) {
                distErrorMessage.textContent = `Minimum distance for race: ${currentMinDistance}m`;
                distErrorMessage.style.display = "block";
                return false;
            } else {
                distErrorMessage.style.display = "none";
            }
        } else {
            distErrorMessage.style.display = "none";
        }
        return true;
    }

    //untuk membuat dropdown race disabled=true jika checkbox tidak dicentang,
    //dan disabled=false jika checkbox dicentang
    checkbox.addEventListener("change", () => {
        dropdown.disabled = !checkbox.checked;
        hiddenIdRace.disabled = checkbox.checked;

        if (checkbox.checked) {
            const selectedOption = dropdown.options[dropdown.selectedIndex];
            if (selectedOption.value !== "-1") {
                currentMinDistance = parseInt(selectedOption.getAttribute("data-jarakTempuh"), 10) || 0;
                hiddenIdRace.value = selectedOption.value;
            } else {
                currentMinDistance = 0;
                hiddenIdRace.value = "-1";
            }
        } else {
            currentMinDistance = 0;
            hiddenIdRace.value = "-1";
        }

        validateDistance();
    });

    dropdown.addEventListener("change", () => {
        const selectedOption = dropdown.options[dropdown.selectedIndex];

        if (selectedOption.value !== "-1") {
            //parse dari String ke integer (base 10)
            currentMinDistance = parseInt(selectedOption.getAttribute("data-jarakTempuh"), 10) || 0;
            hiddenIdRace.value = selectedOption.value;
        } else {
            currentMinDistance = 0;
            hiddenIdRace.value = "-1";
        }

        validateDistance();
    });

    //prevent submit form jika (input jarakTempuh)<(jarakTempuh race)
    distanceInput.addEventListener("input", validateDistance);
    submitButton.addEventListener("click", function (event) {
        let isValid = validateDistance();
        if (!isValid) {
            event.preventDefault();
            alert("Please correct the errors before submitting the form.");
        }
    });
    
    
});
