// Room tab switching
document.addEventListener('DOMContentLoaded', function () {
    // ── Room tabs ──
    var tabs = document.querySelectorAll('.room-tab');
    tabs.forEach(function (tab) {
        tab.addEventListener('click', function () {
            tabs.forEach(function (t) { t.classList.remove('room-tab--active'); });
            tab.classList.add('room-tab--active');
        });
    });

    // ── Booker "I'm guest" checkbox fills in guest form from booker fields ──
    var imGuestCheckbox = document.getElementById('im-guest');
    if (imGuestCheckbox) {
        imGuestCheckbox.addEventListener('change', function () {
            var bookerName = document.getElementById('booker-name') ? document.getElementById('booker-name').value : '';
            var bookerEmail = document.getElementById('booker-email') ? document.getElementById('booker-email').value : '';
            var guestName = document.getElementById('guest-name');
            var guestEmail = document.getElementById('guest-email');
            if (imGuestCheckbox.checked) {
                if (guestName) guestName.value = bookerName;
                if (guestEmail) guestEmail.value = bookerEmail;
            } else {
                if (guestName) guestName.value = '';
                if (guestEmail) guestEmail.value = '';
            }
        });
    }

    // ── Special request / Room detail toggle (Hide details) ──
    var hideDetailsBtn = document.getElementById('hide-room-details');
    var roomDetailContent = document.getElementById('room-detail-content');
    if (hideDetailsBtn && roomDetailContent) {
        hideDetailsBtn.addEventListener('click', function () {
            var isHidden = roomDetailContent.style.display === 'none';
            roomDetailContent.style.display = isHidden ? '' : 'none';
            hideDetailsBtn.querySelector('.toggle-label').textContent = isHidden ? 'Hide details' : 'Show details';
        });
    }

    var hideSpecialBtn = document.getElementById('hide-special-request');
    var specialBody = document.getElementById('special-request-body');
    if (hideSpecialBtn && specialBody) {
        hideSpecialBtn.addEventListener('click', function () {
            var isHidden = specialBody.style.display === 'none';
            specialBody.style.display = isHidden ? '' : 'none';
            hideSpecialBtn.querySelector('.toggle-label').textContent = isHidden ? 'Hide details' : 'Show details';
        });
    }

    // ── Discount code apply ──
    var applyBtn = document.querySelector('.discount-apply-btn');
    if (applyBtn) {
        applyBtn.addEventListener('click', function () {
            var discountInput = document.querySelector('.discount-input');
            if (discountInput && discountInput.value.trim()) {
                alert('Discount code "' + discountInput.value.trim() + '" applied!');
            }
        });
    }

    // ── Payment button ──
    var payBtn = document.querySelector('.payment-btn');
    if (payBtn) {
        payBtn.addEventListener('click', function () {
            // Validate booker name & email
            var bookerName = document.getElementById('booker-name');
            var bookerEmail = document.getElementById('booker-email');
            if (bookerName && !bookerName.value.trim()) {
                bookerName.focus();
                return;
            }
            if (bookerEmail && !bookerEmail.value.trim()) {
                bookerEmail.focus();
                return;
            }
            // In real Spring Boot: form submission via th:action
            alert('Proceeding to payment...');
        });
    }
});
