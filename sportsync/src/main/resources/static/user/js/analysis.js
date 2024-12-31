function renderBarChart(containerId, data, labelKey, valueKey) {
    const container = document.getElementById(containerId);
    container.innerHTML = ''; 
    
    if (data.length === 0) return;

    let maxValue = 0;
    for (let i = 0; i < data.length; i++) {
        if (data[i][valueKey] > maxValue) {
            maxValue = data[i][valueKey];
        }
    }

    const scale = maxValue === 0 ? 0 : 100 / maxValue;

    data.forEach(item => {
        const label = item[labelKey];
        const value = item[valueKey];

        const barContainer = document.createElement('div');
        barContainer.className = 'bar-container';

        const bar = document.createElement('div');
        bar.className = 'bar';
        bar.style.height = `${value * scale}%`;

        const barValue = document.createElement('span');
        barValue.className = 'bar-value';
        barValue.textContent = `${value}m`;

        const barLabel = document.createElement('span');
        barLabel.className = 'bar-label';
        barLabel.textContent = label;

        barContainer.appendChild(barValue);
        barContainer.appendChild(bar);
        barContainer.appendChild(barLabel);

        container.appendChild(barContainer);
    });
}

document.addEventListener('DOMContentLoaded', function() {
    const exportBtn = document.querySelector(".export-btn");
    exportBtn.addEventListener("click", () => {
        print();
    });
    
    renderBarChart('weekChart', weekChartData, 'hari', 'totalJarakTempuh');
    renderBarChart('monthChart', monthChartData, 'minggu', 'totalJarakTempuh');
    renderBarChart('yearChart', yearChartData, 'bulan', 'totalJarakTempuh');
});
