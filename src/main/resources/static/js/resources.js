/* Support hub. Public page: works with or without a login. */
const CATEGORIES = [
  ['HELPLINE', 'Helplines'],
  ['REPORTING', 'How to report'],
  ['SAFETY_TIP', 'Safety tips']
];

function $(id) { return document.getElementById(id); }

function buildNav() {
  const nav = $('nav');
  if (!getToken()) return; // default nav already shows "Log in"
  nav.textContent = '';
  [['dashboard.html', 'SOS'], ['contacts.html', 'Contacts']].forEach(([href, label], i) => {
    const a = document.createElement('a');
    a.href = href; a.textContent = label;
    nav.appendChild(a);
  });
  const btn = document.createElement('button');
  btn.type = 'button'; btn.textContent = 'Log out';
  btn.addEventListener('click', logout);
  nav.appendChild(btn);
}

function renderItem(r) {
  const li = document.createElement('li');
  const title = document.createElement('div');
  title.className = 'name';
  title.textContent = r.title;
  li.appendChild(title);

  const desc = document.createElement('div');
  desc.textContent = r.description;
  li.appendChild(desc);

  const links = document.createElement('div');
  links.className = 'actions';
  if (r.phone) {
    const a = document.createElement('a');
    a.href = 'tel:' + r.phone.replace(/[^\d+]/g, '');
    a.textContent = 'Call ' + r.phone;
    links.appendChild(a);
  }
  if (r.url && /^https:\/\//i.test(r.url)) {
    const a = document.createElement('a');
    a.href = r.url; a.target = '_blank'; a.rel = 'noopener noreferrer';
    a.textContent = 'Visit website';
    links.appendChild(a);
  }
  if (links.children.length) li.appendChild(links);
  return li;
}

function render(items) {
  const box = $('groups');
  box.textContent = '';
  CATEGORIES.forEach(([key, label]) => {
    const group = items.filter(r => r.category === key);
    if (!group.length) return;
    const section = document.createElement('section');
    section.className = 'card';
    const h = document.createElement('h2');
    h.textContent = label;
    section.appendChild(h);
    const ul = document.createElement('ul');
    ul.className = 'contacts';
    group.forEach(r => ul.appendChild(renderItem(r)));
    section.appendChild(ul);
    box.appendChild(section);
  });
}

async function load() {
  $('loading').hidden = false;
  $('load-error').hidden = true;
  try {
    const res = await apiFetch('/api/resources', { auth: false });
    $('loading').hidden = true;
    if (!res.ok) {
      $('load-error-text').textContent = errorText(res.data, 'Could not load support information.');
      $('load-error').hidden = false;
      return;
    }
    render(res.data || []);
  } catch (e) {
    $('loading').hidden = true;
    $('load-error-text').textContent = 'Could not reach the server. If you are in danger, call 10111.';
    $('load-error').hidden = false;
  }
}

buildNav();
$('retry-btn').addEventListener('click', load);
load();
