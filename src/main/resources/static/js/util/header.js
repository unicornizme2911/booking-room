document.addEventListener("DOMContentLoaded", function() {
    const guestSection = document.getElementById('guestSection')
    const userSection = document.getElementById('userSection')
    const usernameEl = document.getElementById('username')
    const dropdown = document.getElementById('userDropdown')

    fetch("/api/v1/auth/me", {
        method: 'GET',
        credentials: "include"
    }).then(res => {
        if (!res.ok) return null;
        return res.json();
    }).then(user => {
        if (!user) throw new Error('Not Found');
        guestSection.classList.add('hidden');
        userSection.classList.remove('hidden');
        usernameEl.innerText = user.lastname || user.email;
    }).catch(() => {
        guestSection.classList.remove('hidden');
        userSection.classList.add('hidden');
    });

    userSection?.addEventListener('click', (e) => {
        e.stopPropagation();
        dropdown.classList.toggle('show');
    })

    document.addEventListener('click', (e) => {
        dropdown.classList.remove('show');
    })

    const logoutBtn = document.getElementById('logoutBtn')
    logoutBtn?.addEventListener('click', (e) => {
        e.preventDefault();
        fetch("/api/v1/auth/logout", {
            method: "POST",
            credentials: "include"
        }).then(() => {
            window.location.href = "/";
        });
    })
})
