export default () => {
    const n = (n, e, t) => n.getAttribute(e) || t;
    const e = (n) => document.getElementsByTagName(n);
    const t = () => {
      let t = e("script"),
        o = t.length,
        i = t[o - 1];
      return {
        l: o,
        z: n(i, "zIndex", -1), 
        o: n(i, "opacity", 0.9),
        c: n(i, "color", "0,0,0"),
        n: n(i, "count", 150),
      };
    };
    const o = () => {
      a = m.width = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
      c = m.height = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
    };
    let stop;
    const i = () => {
      r.clearRect(0, 0, a, c);
      let n, e, t, o, m, l;
      s.forEach(function (i, x) {
        for (
          i.x += i.xa,
          i.y += i.ya,
          i.xa *= i.x > a || i.x < 0 ? -1 : 1,
          i.ya *= i.y > c || i.y < 0 ? -1 : 1,
          r.fillRect(i.x - 0.5, i.y - 0.5, 1, 1),
          e = x + 1;
          e < u.length;
          e++
        )
          (n = u[e]),
            null !== n.x &&
              null !== n.y &&
              ((o = i.x - n.x),
              (m = i.y - n.y),
              (l = o * o + m * m),
              l < n.max &&
                (n === y &&
                  l >= n.max / 2 &&
                  ((i.x -= 0.03 * o), (i.y -= 0.03 * m)),
                (t = (n.max - l) / n.max),
                (r.beginPath()),
                (r.lineWidth = t / 1.5),
                (r.strokeStyle =
                  "rgba(" +
                  ( Math.floor(Math.random() * 10)) + "," + 
                  ( Math.floor(Math.random() * 10)) + "," +
                  ( Math.floor(Math.random() * 100)) + "," +
                  (t + 1) + ")"), 
                r.moveTo(i.x, i.y),
                r.lineTo(n.x, n.y),
                r.stroke()));
      }),
        (stop = x(i));
    };
    let a,
      c,
      u,
      m = document.createElement("canvas"),
      d = t(),
      l = "c_n" + d.l,
      r = m.getContext("2d"),
      x =
        window.requestAnimationFrame ||
        window.webkitRequestAnimationFrame ||
        window.mozRequestAnimationFrame ||
        window.oRequestAnimationFrame ||
        window.msRequestAnimationFrame ||
        function (n) {
          window.setTimeout(n, 1e3 / 45);
        },
      w = Math.random,
      y = {
        x: null,
        y: null,
        max: 2e4,
      };
    (m.id = l),
      (m.style.cssText =
        "position:fixed;top:0;left:0;z-index:" +
        d.z +
        ";opacity:" +
        d.o),
      e("body")[0].appendChild(m),
      o();
    const moveMove = (n) => {
      (n = n || window.event), (y.x = n.clientX), (y.y = n.clientY);
    };
    const mouseOut = () => {
      (y.x = null), (y.y = null);
    };
    window.addEventListener("resize", o),
      window.addEventListener("mousemove", moveMove),
      window.addEventListener("mouseout", mouseOut);
    let s, f;
    for (s = [], f = 0; d.n > f; f++) {
      let h = w() * a,
        g = w() * c,
        v = 2 * w() - 1,
        p = 2 * w() - 1;
      s.push({
        x: h,
        y: g,
        xa: v,
        ya: p,
        max: 6e3,
      });
    }
    u = s.concat([y]);
    return {
      start: () => i(),
      stop: () => {
        window.removeEventListener("resize", o),
          window.removeEventListener("mousemove", moveMove),
          window.removeEventListener("mouseout", mouseOut),
          m.parentNode.removeChild(m),
          stop && window.cancelAnimationFrame(stop);
      },
    };
  };
  