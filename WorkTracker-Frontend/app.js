// ---------------------------------------------------------------------------
// Config and storage
// ---------------------------------------------------------------------------
var API_BASE = 'http://localhost:8080';
var STORAGE_KEY = 'workTrackerUser';

function getCurrentUser() {
  var json = localStorage.getItem(STORAGE_KEY);
  if (!json) return null;
  return JSON.parse(json);
}

function setCurrentUser(user) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
}

function clearCurrentUser() {
  localStorage.removeItem(STORAGE_KEY);
}

// ---------------------------------------------------------------------------
// Screen switching: show login or timer
// ---------------------------------------------------------------------------
function showLoginScreen() {
  document.getElementById('login-screen').style.display = 'block';
  document.getElementById('timer-screen').style.display = 'none';
  document.getElementById('login-message').textContent = '';
  document.getElementById('create-account').style.display = 'none';
}

function showTimerScreen() {
  document.getElementById('login-screen').style.display = 'none';
  document.getElementById('timer-screen').style.display = 'block';
  var user = getCurrentUser();
  document.getElementById('user-name').textContent = user ? user.name : '';
  loadRecentSessions();
  resetTimerUI();
}

// ---------------------------------------------------------------------------
// Login: continue (find by email) or create account
// ---------------------------------------------------------------------------
function onContinueClick() {
  var emailInput = document.getElementById('login-email');
  var email = (emailInput.value || '').trim();
  var msgEl = document.getElementById('login-message');
  var createEl = document.getElementById('create-account');

  if (!email) {
    msgEl.textContent = 'Please enter an email.';
    return;
  }

  msgEl.textContent = '';
  createEl.style.display = 'none';

  fetch(API_BASE + '/api/people')
    .then(function (r) { return r.json(); })
    .then(function (people) {
      var i;
      for (i = 0; i < people.length; i++) {
        if (people[i].email === email) {
          setCurrentUser({ id: people[i].id, name: people[i].name, email: people[i].email });
          showTimerScreen();
          return;
        }
      }
      // Not found: show create form (keep email in the box; we'll use it for create)
      createEl.style.display = 'block';
      msgEl.textContent = '';
    })
    .catch(function (err) {
      msgEl.textContent = 'Could not reach the server. Is the backend running?';
      console.error(err);
    });
}

function onCreateAccountClick() {
  var email = (document.getElementById('login-email').value || '').trim();
  var name = (document.getElementById('create-name').value || '').trim();
  var password = document.getElementById('create-password').value || '';
  var msgEl = document.getElementById('login-message');

  if (!email || !name) {
    msgEl.textContent = 'Please enter email and name.';
    return;
  }

  fetch(API_BASE + '/api/people', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name: name, email: email, password: password })
  })
    .then(function (r) {
      if (!r.ok) {
        return r.text().then(function (t) { throw new Error(t || r.status); });
      }
      return r.json();
    })
    .then(function (person) {
      setCurrentUser({ id: person.id, name: person.name, email: person.email });
      showTimerScreen();
    })
    .catch(function (err) {
      msgEl.textContent = err.message || 'Failed to create account.';
      console.error(err);
    });
}

// ---------------------------------------------------------------------------
// Timer: start (remember time in memory), stop (POST session to API)
// ---------------------------------------------------------------------------
var currentStartTime = null; // Date when user clicked Start

function resetTimerUI() {
  currentStartTime = null;
  document.getElementById('btn-start').disabled = false;
  document.getElementById('btn-stop').disabled = true;
  document.getElementById('timer-status').style.display = 'none';
}

function onStartClick() {
  currentStartTime = new Date();
  document.getElementById('btn-start').disabled = true;
  document.getElementById('btn-stop').disabled = false;
  document.getElementById('timer-status').style.display = 'block';
  document.getElementById('timer-status').textContent = 'Timer running... (click Stop when done)';
}

function onStopClick() {
  if (!currentStartTime) return;
  var user = getCurrentUser();
  if (!user) return;

  var endTime = new Date();
  var subject = (document.getElementById('session-subject').value || '').trim();
  var name = (document.getElementById('session-name').value || '').trim();
  var description = (document.getElementById('session-description').value || '').trim();

  var body = {
    personId: user.id,
    startTime: currentStartTime.toISOString(),
    endTime: endTime.toISOString(),
    subject: subject || 'Work',
    name: name || null,
    description: description || null
  };

  fetch(API_BASE + '/api/work-sessions', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  })
    .then(function (r) {
      if (!r.ok) throw new Error('Failed to save session');
      return r.json();
    })
    .then(function () {
      resetTimerUI();
      loadRecentSessions();
    })
    .catch(function (err) {
      console.error(err);
      alert('Failed to save session. See console.');
    });
}

function loadRecentSessions() {
  var user = getCurrentUser();
  if (!user) return;
  var listEl = document.getElementById('recent-sessions');
  listEl.innerHTML = '';

  fetch(API_BASE + '/api/work-sessions?personId=' + user.id)
    .then(function (r) { return r.json(); })
    .then(function (sessions) {
      var i;
      for (i = 0; i < sessions.length && i < 10; i++) {
        var s = sessions[i];
        var start = s.startTime ? new Date(s.startTime).toLocaleString() : '';
        var end = s.endTime ? new Date(s.endTime).toLocaleString() : '';
        var sub = s.subject || 'Work';
        var li = document.createElement('li');
        li.textContent = sub + ' — ' + start + ' to ' + end;
        listEl.appendChild(li);
      }
    })
    .catch(function (err) {
      listEl.innerHTML = '<li>Could not load sessions.</li>';
      console.error(err);
    });
}

function onLogoutClick() {
  clearCurrentUser();
  showLoginScreen();
}

// ---------------------------------------------------------------------------
// Wire up buttons and initial screen
// ---------------------------------------------------------------------------
document.addEventListener('DOMContentLoaded', function () {
  document.getElementById('btn-continue').addEventListener('click', onContinueClick);
  document.getElementById('btn-create').addEventListener('click', onCreateAccountClick);
  document.getElementById('btn-logout').addEventListener('click', onLogoutClick);
  document.getElementById('btn-start').addEventListener('click', onStartClick);
  document.getElementById('btn-stop').addEventListener('click', onStopClick);

  if (getCurrentUser()) {
    showTimerScreen();
  } else {
    showLoginScreen();
  }
});
