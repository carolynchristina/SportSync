const overlay = document.getElementById('OverlayIconUser')
const iconUser = document.getElementsByClassName('userProfile')
function showElement(){
    if(overlay.style.visibility == 'hidden'){
        overlay.style.visibility = 'visible';
    }
    else{
        overlay.style.visibility = 'hidden';
    }
}
function hideElement(){
    overlay.style.visibility = 'hidden';
}
iconUser.addEventListener('click', showElement);