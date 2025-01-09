document.addEventListener("DOMContentLoaded", function () {
    const mainSection = document.querySelector('.Main1');
    const images = [
        "/user/assets/LandingPage/LandingPageBackground2.jpg",
        "/user/assets/LandingPage/LandingPageBackground3.jpg",
        "/user/assets/LandingPage/LandingPageBackground1.svg"
    ];
    let currentIndex = 0;

    function changeBackground() {
        mainSection.style.backgroundImage = `url(${images[currentIndex]})`;
        currentIndex = (currentIndex + 1) % images.length;
    }

    setInterval(changeBackground, 5000); // Ganti gambar setiap 5 detik
});
