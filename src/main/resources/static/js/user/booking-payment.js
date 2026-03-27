document.addEventListener("DOMContentLoaded", () => {
    const payBtn = document.getElementById('payBtn');

    payBtn.addEventListener('click', async function () {

        payBtn.disabled = true;
        document.getElementById('loading').classList.remove('hidden');
        const reservationId = payBtn.dataset.reservationId;
        const res = await fetch(`/booking/payment/vnpay?reservationId=${reservationId}`, {
            method: 'POST'
        });

        const data = await res.text();
        window.location.href = data;
    });
})