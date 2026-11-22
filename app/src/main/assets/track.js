(function () {
  if (window.__ssTrack) return; window.__ssTrack = true;
  var SEL = ['[class*="now"] [class*="title"]','[class*="player"] [class*="title"]','[class*="track-title"]'];
  var els = new Set(), cur = null, sess = 0, last = Date.now();

  var op = HTMLMediaElement.prototype.play;
  HTMLMediaElement.prototype.play = function () { els.add(this); return op.apply(this, arguments); };

  function title() {
    try { var m = navigator.mediaSession && navigator.mediaSession.metadata; if (m && m.title) return m.title; } catch (e) {}
    for (var i = 0; i < SEL.length; i++) {
      var e = document.querySelector(SEL[i]);
      if (e && e.textContent.trim()) return e.textContent.trim();
    }
    var a = cur && (cur.currentSrc || cur.src);
    if (a) { try { return decodeURIComponent(a.split('?')[0].split('/').pop()).replace(/\.[a-z0-9]+$/i, ''); } catch (e) {} }
    return 'Bilinmeyen';
  }

  var pending = 0, pendTitle = null, lastTitle = null;
  function flush() { if (pending > 0 && pendTitle) { Android.tick(pendTitle, pending); pending = 0; } }

  setInterval(function () {
    var now = Date.now(), dt = Math.min((now - last) / 1000, 5); last = now;
    document.querySelectorAll('audio,video').forEach(function (e) { els.add(e); });
    var playing = null;
    els.forEach(function (e) { if (!e.paused && !e.ended && e.readyState > 2) playing = e; });
    if (!playing) { flush(); return; }
    cur = playing;
    var t = title();
    if (t !== lastTitle) { flush(); lastTitle = t; sess = 0; }
    pendTitle = t; pending += dt;
    var before = sess; sess += dt;
    if (before < 30 && sess >= 30) Android.play(t);
    if (pending >= 5) { var s = Math.round(pending); pending -= s; Android.tick(t, s); }
  }, 1000);
})();
