/* Trusted contacts screen. All messages come from the server's real responses. */
const FIELDS = ['name', 'phone', 'email', 'relationship'];
let editingId = null;

if (requireLogin()) init();

function $(id) { return document.getElementById(id); }

function init() {
  $('logout-btn').addEventListener('click', logout);
  $('contact-form').addEventListener('submit', onSubmit);
  $('cancel-btn').addEventListener('click', resetForm);
  $('retry-btn').addEventListener('click', loadContacts);
  loadContacts();
}

function showStatus(kind, text) {
  const box = $('status');
  box.textContent = '';
  if (!text) return;
  const div = document.createElement('div');
  div.className = 'msg ' + kind;
  div.textContent = text;
  box.appendChild(div);
}

function clearFieldErrors() {
  FIELDS.forEach(f => {
    $('err-' + f).hidden = true;
    $('err-' + f).textContent = '';
    $(f).removeAttribute('aria-invalid');
  });
}

function showFieldErrors(fieldErrors) {
  let first = null;
  FIELDS.forEach(f => {
    if (fieldErrors && fieldErrors[f]) {
      $('err-' + f).textContent = fieldErrors[f];
      $('err-' + f).hidden = false;
      $(f).setAttribute('aria-invalid', 'true');
      if (!first) first = $(f);
    }
  });
  if (first) first.focus();
}

function resetForm() {
  editingId = null;
  $('contact-form').reset();
  clearFieldErrors();
  $('form-title').textContent = 'Add a contact';
  $('save-btn').textContent = 'Add contact';
  $('cancel-btn').hidden = true;
}

function startEdit(c) {
  editingId = c.id;
  clearFieldErrors();
  showStatus('', '');
  $('name').value = c.name || '';
  $('phone').value = c.phone || '';
  $('email').value = c.email || '';
  $('relationship').value = c.relationship || '';
  $('form-title').textContent = 'Edit contact';
  $('save-btn').textContent = 'Save changes';
  $('cancel-btn').hidden = false;
  $('contact-form').scrollIntoView({ behavior: 'smooth', block: 'start' });
  $('name').focus();
}

async function onSubmit(ev) {
  ev.preventDefault();
  const btn = $('save-btn');
  if (btn.disabled) return;
  btn.disabled = true;
  clearFieldErrors();
  showStatus('info', 'Saving...');
  const body = {
    name: $('name').value,
    phone: $('phone').value,
    email: $('email').value,
    relationship: $('relationship').value
  };
  const wasEditing = editingId !== null;
  try {
    const res = wasEditing
      ? await apiFetch('/api/contacts/' + editingId, { method: 'PUT', body })
      : await apiFetch('/api/contacts', { method: 'POST', body });
    if (res.ok) {
      resetForm();
      showStatus('ok', wasEditing ? 'Contact updated.' : 'Contact added.');
      await loadContacts();
    } else if (res.status === 400 && res.data && res.data.fieldErrors) {
      showStatus('bad', 'Please fix the highlighted fields.');
      showFieldErrors(res.data.fieldErrors);
    } else if (res.status === 404) {
      resetForm();
      showStatus('bad', errorText(res.data, 'That contact no longer exists.'));
      await loadContacts();
    } else if (res.status !== 401) {
      showStatus('bad', 'Could not save the contact: ' + errorText(res.data, 'Please try again.'));
    }
  } catch (e) {
    showStatus('bad', 'Could not reach the server. The contact was NOT saved.');
  } finally {
    btn.disabled = false;
  }
}

async function onDelete(c, button) {
  if (!window.confirm('Delete ' + c.name + ' from your trusted contacts?')) return;
  button.disabled = true;
  showStatus('info', 'Deleting...');
  try {
    const res = await apiFetch('/api/contacts/' + c.id, { method: 'DELETE' });
    if (res.ok) {
      if (editingId === c.id) resetForm();
      showStatus('ok', 'Contact deleted.');
    } else if (res.status === 404) {
      showStatus('warn', errorText(res.data, 'That contact no longer exists.'));
    } else if (res.status !== 401) {
      showStatus('bad', 'Could not delete the contact: ' + errorText(res.data, 'Please try again.'));
    }
    await loadContacts();
  } catch (e) {
    showStatus('bad', 'Could not reach the server. The contact was NOT deleted.');
    button.disabled = false;
  }
}

function renderContacts(list) {
  const ul = $('contacts');
  ul.textContent = '';
  list.forEach(c => {
    const li = document.createElement('li');

    const name = document.createElement('div');
    name.className = 'name';
    name.textContent = c.name + (c.relationship ? ' (' + c.relationship + ')' : '');
    li.appendChild(name);

    const phone = document.createElement('div');
    const call = document.createElement('a');
    call.href = 'tel:' + String(c.phone).replace(/[^+\d]/g, '');
    call.textContent = c.phone;
    phone.appendChild(call);
    li.appendChild(phone);

    if (c.email) {
      const email = document.createElement('div');
      email.className = 'muted';
      email.textContent = c.email;
      li.appendChild(email);
    }

    const actions = document.createElement('div');
    actions.className = 'actions';
    const edit = document.createElement('button');
    edit.type = 'button';
    edit.className = 'secondary';
    edit.textContent = 'Edit';
    edit.setAttribute('aria-label', 'Edit ' + c.name);
    edit.addEventListener('click', () => startEdit(c));
    const del = document.createElement('button');
    del.type = 'button';
    del.className = 'danger';
    del.textContent = 'Delete';
    del.setAttribute('aria-label', 'Delete ' + c.name);
    del.addEventListener('click', () => onDelete(c, del));
    actions.append(edit, del);
    li.appendChild(actions);

    ul.appendChild(li);
  });
}

async function loadContacts() {
  $('loading').hidden = false;
  $('load-error').hidden = true;
  $('empty').hidden = true;
  $('contacts').textContent = '';
  try {
    const res = await apiFetch('/api/contacts');
    $('loading').hidden = true;
    if (!res.ok) {
      if (res.status !== 401) {
        $('load-error-text').textContent = 'Could not load your contacts: ' + errorText(res.data, 'Server error.');
        $('load-error').hidden = false;
      }
      return;
    }
    if (res.data.length === 0) $('empty').hidden = false;
    else renderContacts(res.data);
  } catch (e) {
    $('loading').hidden = true;
    $('load-error-text').textContent = 'Could not reach the server. Your contacts could not be loaded.';
    $('load-error').hidden = false;
  }
}
