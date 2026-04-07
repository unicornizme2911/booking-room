'use strict';

// ── Navbar scroll behaviour ──────────────────────────────────
(function initNavbar() {
    const navbar = document.getElementById('navbar');
    const navToggle = document.getElementById('navToggle');
    const mobileMenu = document.getElementById('mobileMenu');
    if (!navbar) return;

    function onScroll() {
        navbar.classList.toggle('scrolled', window.scrollY > 50);
    }
    window.addEventListener('scroll', onScroll, { passive: true });
    onScroll(); // run once on load

    if (navToggle && mobileMenu) {
        navToggle.addEventListener('click', () => {
            mobileMenu.classList.toggle('open');
        });
        // Close menu on outside click
        document.addEventListener('click', (e) => {
            if (!navbar.contains(e.target)) mobileMenu.classList.remove('open');
        });
    }
})();


// ── Fade-up on scroll ────────────────────────────────────────
(function initFadeUp() {
    const els = document.querySelectorAll('.fade-up');
    if (!els.length) return;

    const observer = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {
                const el = entry.target;
                const delay = el.style.getPropertyValue('--delay') || '0s';
                setTimeout(() => el.classList.add('visible'), parseFloat(delay) * 1000);
                observer.unobserve(el);
            }
        });
    }, { threshold: 0.12, rootMargin: '0px 0px -40px 0px' });

    els.forEach((el) => observer.observe(el));
})();

function createCarousel(opts) {
    const track   = document.getElementById(opts.trackId);
    const prevBtn = document.getElementById(opts.prevId);
    const nextBtn = document.getElementById(opts.nextId);
    const dotsWrap = document.getElementById(opts.dotsId);
    if (!track) return;

    const cards = Array.from(track.children);
    const visible = opts.visibleCount || 3;
    const total   = cards.length;
    let current   = 0;

    // Build dots
    if (dotsWrap) {
        dotsWrap.innerHTML = '';
        for (let i = 0; i <= total - visible; i++) {
            const dot = document.createElement('button');
            dot.setAttribute('aria-label', `Slide ${i + 1}`);
            if (i === 0) dot.classList.add('active');
            dot.addEventListener('click', () => goTo(i));
            dotsWrap.appendChild(dot);
        }
    }

    function updateDots() {
        if (!dotsWrap) return;
        dotsWrap.querySelectorAll('button').forEach((dot, i) => {
            dot.classList.toggle('active', i === current);
        });
    }

    function goTo(index) {
        const max = Math.max(0, total - visible);
        current = Math.max(0, Math.min(index, max));
        const cardWidth = track.parentElement.offsetWidth;
        const gap = 20; // must match CSS gap
        const offset = current * ((cardWidth + gap) / visible);
        track.style.transform = `translateX(-${offset}px)`;
        updateDots();
    }

    if (prevBtn) prevBtn.addEventListener('click', () => goTo(current - 1));
    if (nextBtn) nextBtn.addEventListener('click', () => goTo(current + 1));

    // Touch / swipe support
    let startX = 0;
    track.parentElement.addEventListener('touchstart', (e) => { startX = e.touches[0].clientX; }, { passive: true });
    track.parentElement.addEventListener('touchend',   (e) => {
        const diff = startX - e.changedTouches[0].clientX;
        if (Math.abs(diff) > 40) goTo(current + (diff > 0 ? 1 : -1));
    });

    // Recalculate on resize
    window.addEventListener('resize', () => goTo(current));
}

// News carousel
createCarousel({
    trackId: 'newsTrack',
    prevId:  'newsPrev',
    nextId:  'newsNext',
    dotsId:  'newsDots',
    visibleCount: 3,
});

// Testimonials carousel
createCarousel({
    trackId: 'testTrack',
    prevId:  'testPrev',
    nextId:  'testNext',
    dotsId:  'testDots',
    visibleCount: 3,
});


// ── Wishlist toggle ──────────────────────────────────────────
(function initWishlist() {
    document.querySelectorAll('.prop-card__wishlist').forEach((btn) => {
        btn.addEventListener('click', function () {
            this.classList.toggle('saved');
            this.textContent = this.classList.contains('saved') ? '♥' : '♡';
            this.style.color = this.classList.contains('saved') ? 'var(--accent)' : '';
        });
    });
})();


// ── Search form – date guard ──────────────────────────────────
(function initSearchForm() {
    const form     = document.getElementById('searchForm');
    if (!form) return;
    const checkin  = form.querySelector('[name="checkin"]');
    const checkout = form.querySelector('[name="checkout"]');

    if (checkin) {
        // Set min = today
        const today = new Date().toISOString().split('T')[0];
        checkin.setAttribute('min', today);
        checkin.addEventListener('change', () => {
            if (checkout) checkout.setAttribute('min', checkin.value);
        });
    }

    form.addEventListener('submit', (e) => {
        const loc = form.querySelector('[name="location"]');
        if (loc && !loc.value.trim()) {
            e.preventDefault();
            loc.style.borderColor = 'var(--accent)';
            loc.focus();
            loc.addEventListener('input', () => { loc.style.borderColor = ''; }, { once: true });
        }
    });
})();


// ── Newsletter form ──────────────────────────────────────────
(function initNewsletter() {
    const form = document.getElementById('newsletterForm');
    if (!form) return;
    form.addEventListener('submit', (e) => {
        // For demo only — remove this block in Spring Boot (let Thymeleaf handle submission)
        e.preventDefault();
        const input = form.querySelector('input[type="email"]');
        const btn   = form.querySelector('button[type="submit"]');
        const originalText = btn.textContent;
        btn.textContent = '✓ Subscribed!';
        btn.style.background = '#27AE60';
        input.value = '';
        setTimeout(() => {
            btn.textContent = originalText;
            btn.style.background = '';
        }, 3000);
    });
})();


// ── Pause marquee on hover ────────────────────────────────────
(function initMarquee() {
    const track = document.getElementById('partnersTrack');
    if (!track) return;
    const wrap = track.parentElement;
    wrap.addEventListener('mouseenter', () => { track.style.animationPlayState = 'paused'; });
    wrap.addEventListener('mouseleave', () => { track.style.animationPlayState = 'running'; });
})();
