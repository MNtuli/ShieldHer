/* Light/dark theme (remembers the choice) and the Quick exit button. Loaded in <head> to avoid a flash. */
(function () {
  var KEY = 'shieldher_theme';
  var root = document.documentElement;
  function stored() { try { return localStorage.getItem(KEY); } catch (e) { return null; } }
  function store(v) { try { localStorage.setItem(KEY, v); } catch (e) { /* storage blocked */ } }
  var saved = stored();
  if (saved === 'light' || saved === 'dark') root.setAttribute('data-theme', saved);

  function current() {
    return root.getAttribute('data-theme') ||
      (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');
  }
  var SUN = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" aria-hidden="true"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/></svg>';
  var MOON = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M21 12.8A9 9 0 1 1 11.2 3a7 7 0 0 0 9.8 9.8Z"/></svg>';

  document.addEventListener('DOMContentLoaded', function () {
    var bar = document.querySelector('header.bar');
    if (bar) {
      var btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'icon-btn';
      var paint = function () {
        var dark = current() === 'dark';
        btn.innerHTML = dark ? SUN : MOON;
        btn.setAttribute('aria-label', dark ? 'Switch to light mode' : 'Switch to dark mode');
        btn.title = btn.getAttribute('aria-label');
      };
      btn.addEventListener('click', function () {
        var next = current() === 'dark' ? 'light' : 'dark';
        root.setAttribute('data-theme', next);
        store(next);
        paint();
      });
      paint();
      (bar.querySelector('nav') || bar).appendChild(btn);
    }

    var exit = document.createElement('button');
    exit.type = 'button';
    exit.className = 'quick-exit';
    exit.textContent = 'Quick exit';
    exit.title = 'Leave this site immediately';
    exit.addEventListener('click', function () { window.location.replace('https://www.google.com'); });
    document.body.appendChild(exit);
  });
})();
