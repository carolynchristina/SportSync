document.addEventListener('DOMContentLoaded', () => {
    const overlay = document.getElementById('OverlayIconUser');
    const iconUser = document.getElementById('profileIcon');

    iconUser.addEventListener('click', () => toggleOverlay(overlay));
});

function toggleOverlay(overlay) {
    if (overlay.style.visibility === 'visible') {
        overlay.style.visibility = 'hidden';
    } else {
        overlay.style.visibility = 'visible';
    }
}
