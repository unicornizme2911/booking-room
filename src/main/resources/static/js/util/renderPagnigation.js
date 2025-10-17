function renderPagnigation(
    totalPages = 0,
    number = 0
) {
    return `
            <li class="page-item ${number === 0 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${number - 1 ? number - 1 : 0}" tabindex="-1" aria-disabled="true">Previous</a>
            </li>
            ${Array.from({length: totalPages}, (_, i) => `
                <li class="page-item ${i === number ? 'active' : ''}">
                    <a class="page-link" data-page="${i}" href="#">${i + 1}</a>
                </li>
            `).join('')}
            <li class="page-item ${number === totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${number + 1}">Next</a>
            </li>  
            `;
}