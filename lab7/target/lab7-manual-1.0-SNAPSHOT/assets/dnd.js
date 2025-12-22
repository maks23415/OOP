(() => {
    const nameInput = document.getElementById('charName');
    const backgroundInput = document.getElementById('charBackground');
    const classSelect = document.getElementById('charClass');
    const groupInput = document.getElementById('charGroup');
    const levelValue = document.getElementById('levelValue');
    const classDetails = document.getElementById('classDetails');
    const validationMessage = document.getElementById('validationMessage');
    const statusMessage = document.getElementById('statusMessage');
    const rollAllBtn = document.getElementById('rollAll');
    const saveBtn = document.getElementById('saveCharacter');
    const newBtn = document.getElementById('newCharacter');
    const statsGrid = document.getElementById('statsGrid');
    const freeRollBtn = document.getElementById('freeRoll');
    const freeRollValue = document.getElementById('freeRollValue');
    const assignSelect = document.getElementById('assignSelect');
    const assignBtn = document.getElementById('assignRoll');
    const hpValue = document.getElementById('hpValue');
    const damageIntValue = document.getElementById('damageIntValue');
    const damageChaValue = document.getElementById('damageChaValue');
    const rollDamageIntBtn = document.getElementById('rollDamageInt');
    const rollDamageChaBtn = document.getElementById('rollDamageCha');
    const themeToggle = document.getElementById('themeToggle');
    const battleStatus = document.getElementById('battleStatus');
    const enemyStatus = document.getElementById('enemyStatus');
    const battleLog = document.getElementById('battleLog');
    const battleStartBtn = document.getElementById('battleStart');
    const battleAttackBtn = document.getElementById('battleAttack');
    const battleRestBtn = document.getElementById('battleRest');

    const STORAGE_KEY = 'grunge-sheet-character';
    const THEME_KEY = 'lab7Theme';

    const classMap = {
        leader: {
            name: 'Староста',
            mods: { charisma: 2, luck: 1 },
            blurb: 'Ответственный студент, организующий мероприятия и помогающий группе.',
        },
        nerd: {
            name: 'Умник',
            mods: { intelligence: 2, luck: -1, charisma: -1 },
            blurb: 'Отличник, знающий все предметы и живущий между парами и лабораторками.',
        },
        vibe: {
            name: 'Вайбкодер',
            mods: { dexterity: 2, luck: 2 },
            blurb: 'Расслабленный программист, пишущий код под хорошую музыку.',
        },
    };

    const statKeys = [
        { key: 'strength', label: 'Сила' },
        { key: 'dexterity', label: 'Ловкость' },
        { key: 'constitution', label: 'Выносливость' },
        { key: 'intelligence', label: 'Интеллект' },
        { key: 'charisma', label: 'Харизма' },
        { key: 'luck', label: 'Удача' },
    ];

    const state = {
        level: 1,
        stats: Object.fromEntries(statKeys.map(({ key }) => [key, { base: null, total: null }])),
        classId: '',
        freeRoll: null,
        xp: 0,
        rests: 3,
        battleIndex: 0,
        playerHp: null,
        playerMax: null,
        enemy: null,
        enemyTurn: 1,
        aiPenalty: 0,
    };

    const labs = [
        { id: 'lab1', name: 'Лаба 1: Hello World', hp: lvl => 15 + lvl * 5, dmg: () => randInt(1, 4) + 1, def: lvl => 8 + lvl, reward: 25, special: 'firstHit' },
        { id: 'lab2', name: 'Лаба 2: Калькулятор', hp: lvl => 25 + lvl * 5, dmg: () => randInt(1, 6) + 2, def: lvl => 10 + lvl, reward: 35, special: 'doubleEveryThird' },
        { id: 'lab3', name: 'Лаба 3: Массив данных', hp: lvl => 35 + lvl * 5, dmg: () => randInt(1, 6) + 3, def: lvl => 12 + lvl, reward: 50, special: 'counter' },
        { id: 'lab4', name: 'Лаба 4: База данных', hp: lvl => 50 + lvl * 5, dmg: () => randInt(1, 8) + 4, def: lvl => 14 + lvl, reward: 75, special: 'regen' },
        { id: 'lab5', name: 'Лаба 5: Веб-сервер', hp: lvl => 65 + lvl * 5, dmg: () => randInt(1, 8) + 5, def: lvl => 16 + lvl, reward: 100, special: 'reflect' },
        { id: 'lab6', name: 'Лаба 6: Многопоточность', hp: lvl => 80 + lvl * 5, dmg: () => randInt(1, 10) + 6, def: lvl => 18 + lvl, reward: 150, special: 'doubleHit' },
        { id: 'lab7', name: 'Лаба 7: Искусственный интеллект', hp: lvl => 100 + lvl * 5, dmg: () => randInt(1, 12) + 7, def: lvl => 20 + lvl, reward: 200, special: 'learning' },
        { id: 'boss', name: 'Финальный босс: Экзаменационная комиссия', hp: lvl => 150 + lvl * 10, dmg: () => randInt(1, 8) + randInt(1, 8) + 8, def: lvl => 22 + lvl, reward: 500, special: 'boss' },
    ];

    function updateClassCallout() {
        const classInfo = classMap[state.classId];
        if (!classInfo) {
            classDetails.textContent = 'Выберите класс, чтобы увидеть бонусы.';
            return;
        }
        const mods = classInfo.mods;
        const parts = Object.entries(mods).map(([k, v]) => {
            const sign = v > 0 ? '+' : '';
            const label = statKeys.find(s => s.key === k)?.label || k;
            return `${label}: ${sign}${v}`;
        });
        classDetails.textContent = `${classInfo.name}: ${classInfo.blurb}\nМодификаторы: ${parts.join(', ')}`;
    }

    function randomD20() {
        return Math.floor(Math.random() * 20) + 1;
    }

    function renderStats() {
        if (!statsGrid) return;
        statKeys.forEach(({ key }) => {
            const card = statsGrid.querySelector(`[data-stat="${key}"]`);
            if (!card) return;
            const baseEl = card.querySelector('.stat-base');
            const modEl = card.querySelector('.stat-mod');
            const totalEl = card.querySelector('.stat-total');
            const classInfo = classMap[state.classId];
            const modValue = classInfo?.mods[key] ?? 0;

            if (state.stats[key].base === null) {
                baseEl.textContent = '—';
                totalEl.textContent = '—';
            } else {
                baseEl.textContent = state.stats[key].base;
                const total = state.stats[key].base + modValue;
                state.stats[key].total = total;
                totalEl.textContent = total;
            }
            const sign = modValue > 0 ? '+' : '';
            modEl.textContent = `${sign}${modValue}`;
        });
        updateMetrics();
    }

    function rollStat(key) {
        state.stats[key].base = randomD20();
        flashCard(key);
        renderStats();
        statusMessage.textContent = `Характеристика «${statKeys.find(s => s.key === key)?.label}» брошена.`;
    }

    function rollAllStats() {
        statKeys.forEach(({ key }) => rollStat(key));
        statusMessage.textContent = 'Все характеристики брошены d20.';
    }

    function rollFree() {
        state.freeRoll = randomD20();
        if (freeRollValue) freeRollValue.textContent = state.freeRoll;
        statusMessage.textContent = 'Свободный бросок готов — выберите характеристику и примените.';
        animateButton(freeRollBtn);
    }

    function assignFreeRoll() {
        if (state.freeRoll === null) {
            statusMessage.textContent = 'Сначала выполните свободный бросок d20.';
            return;
        }
        const target = assignSelect?.value;
        if (!target) {
            statusMessage.textContent = 'Выберите характеристику для назначения броска.';
            return;
        }
        state.stats[target].base = state.freeRoll;
        flashCard(target);
        renderStats();
        statusMessage.textContent = `Свободный бросок ${state.freeRoll} применён к «${statKeys.find(s => s.key === target)?.label}».`;
    }

    function calcTotals() {
        const classInfo = classMap[state.classId];
        return Object.fromEntries(statKeys.map(({ key }) => {
            const base = state.stats[key].base;
            const mod = classInfo?.mods[key] ?? 0;
            return [key, base === null ? null : base + mod];
        }));
    }

    function updateMetrics() {
        const totals = calcTotals();
        const strength = totals.strength;
        const constitution = totals.constitution;
        const intelligence = totals.intelligence;
        const charisma = totals.charisma;
        const luck = totals.luck;

        if (hpValue) {
            if (strength === null || constitution === null) {
                hpValue.textContent = '—';
            } else {
                hpValue.textContent = constitution * 2 + strength;
                flashMetric(hpValue);
            }
        }

        if (damageIntValue) {
            damageIntValue.textContent = intelligence === null ? '—' : `${Math.floor(intelligence / 2)} + 1d6`;
            if (intelligence !== null) flashMetric(damageIntValue);
        }
        if (damageChaValue) {
            if (charisma === null || luck === null) {
                damageChaValue.textContent = '—';
            } else {
                const base = Math.floor(charisma / 2) + Math.floor(luck / 4);
                damageChaValue.textContent = `${base} + 1d6`;
                flashMetric(damageChaValue);
            }
        }
    }

    function rollDamage(type) {
        const totals = calcTotals();
        const d6 = Math.floor(Math.random() * 6) + 1;
        if (type === 'int') {
            if (totals.intelligence === null) {
                statusMessage.textContent = 'Сначала бросьте Интеллект.';
                return;
            }
            const base = Math.floor(totals.intelligence / 2);
            const total = base + d6;
            damageIntValue.textContent = `${base} + d6(${d6}) = ${total}`;
            statusMessage.textContent = 'Урон по формуле интеллекта рассчитан.';
            animateButton(rollDamageIntBtn);
            flashMetric(damageIntValue);
        } else {
            if (totals.charisma === null || totals.luck === null) {
                statusMessage.textContent = 'Сначала бросьте Харизму и Удачу.';
                return;
            }
            const base = Math.floor(totals.charisma / 2) + Math.floor(totals.luck / 4);
            const total = base + d6;
            damageChaValue.textContent = `${base} + d6(${d6}) = ${total}`;
            statusMessage.textContent = 'Урон по формуле харизмы рассчитан.';
            animateButton(rollDamageChaBtn);
            flashMetric(damageChaValue);
        }
    }

    function enemyDamage(enemy) {
        const lab = labs.find(l => l.id === enemy.id);
        return lab ? lab.dmg() : randInt(1, 6);
    }

    function handleEnemyTurn() {
        if (!state.enemy) return;
        const derived = derivePlayerStats();
        const enemy = state.enemy;
        if (enemy.special === 'regen') {
            enemy.hp += 5;
            logBattle(`${enemy.name} восстанавливает 5 HP (теперь ${enemy.hp}).`);
        }
        const hitPlayer = () => {
            const roll = randInt(1, 20) + Math.floor(state.level / 2) + 2;
            return roll >= derived.ac;
        };
        const applyHit = (dmg) => {
            state.playerHp = Math.max(0, state.playerHp - Math.max(1, dmg));
            logBattle(`${enemy.name} наносит ${dmg} урона. Ваши HP: ${state.playerHp}/${derived.maxHp}`);
        };

        const act = () => {
            let dmg = enemyDamage(enemy);
            if (enemy.special === 'doubleEveryThird' && enemy.turn % 3 === 0) {
                dmg *= 2;
                logBattle(`${enemy.name} усиливает удар!`);
            }
            if (hitPlayer()) {
                applyHit(dmg);
            } else {
                logBattle(`${enemy.name} промахивается.`);
            }
        };

        if (enemy.special === 'doubleHit') {
            act();
            act();
        } else {
            act();
        }
        enemy.turn += 1;
        updateBattleStatus();
        if (state.playerHp <= 0) {
            statusMessage.textContent = 'Вы пали в бою. Попробуйте снова.';
            battleAttackBtn?.setAttribute('disabled', 'disabled');
            logBattle('Поражение. Используйте отдых или перебросьте характеристики.');
        }
    }

    function handleWin() {
        const enemy = state.enemy;
        if (!enemy) return;
        const fatigue = Math.max(1, Math.floor(state.playerHp * 0.9));
        state.playerHp = fatigue;
        state.xp += enemy.reward;
        logBattle(`Победа! Получено ${enemy.reward} XP. Усталость: HP теперь ${fatigue}.`);
        state.enemy = null;
        state.battleIndex += 1;
        battleAttackBtn?.setAttribute('disabled', 'disabled');
        while (state.xp >= state.level * 120) {
            state.xp -= state.level * 120;
            state.level += 1;
            levelValue.textContent = state.level;
            logBattle(`Повышение уровня! Теперь уровень ${state.level}.`);
        }
        const derived = derivePlayerStats();
        state.playerMax = derived.maxHp;
        state.playerHp = Math.min(state.playerHp, derived.maxHp);
        updateMetrics();
        updateBattleStatus();
    }

    function playerAttack() {
        if (!state.enemy) {
            statusMessage.textContent = 'Сначала начните бой с лабой.';
            return;
        }
        const enemy = state.enemy;
        const derived = derivePlayerStats();
        const attackRoll = randInt(1, 20);
        const totalRoll = attackRoll + derived.attackBonus;
        let hit = totalRoll >= enemy.defense;
        if (enemy.special === 'firstHit' && enemy.turn === 1) hit = true;
        if (enemy.special === 'boss') {
            const threshold = Math.max(2, 15 - derived.luckBonus);
            if (attackRoll < threshold) hit = false;
        }
        if (!hit) {
            logBattle(`Вы промахнулись (бросок ${attackRoll}+${derived.attackBonus}).`);
            handleEnemyTurn();
            return;
        }
        let dmg = randInt(1, 6) + derived.baseDamageBonus;
        let crit = false;
        if (Math.random() < derived.critChance) {
            crit = true;
            dmg *= 2;
        }
        if (enemy.special === 'learning') {
            dmg = Math.max(1, dmg - state.aiPenalty);
            state.aiPenalty += 1;
        }
        enemy.hp = Math.max(0, enemy.hp - dmg);
        logBattle(`Вы попали по ${enemy.name} на ${dmg} урона${crit ? ' (крит!)' : ''}. Осталось HP: ${enemy.hp}`);

        if (enemy.special === 'reflect' && Math.random() < 0.25) {
            const reflect = Math.max(1, Math.floor(dmg * 0.3));
            state.playerHp = Math.max(0, state.playerHp - reflect);
            logBattle(`${enemy.name} отражает ${reflect} урона обратно! Ваши HP: ${state.playerHp}`);
        }
        if (enemy.special === 'counter' && Math.random() < 0.5) {
            const counter = Math.floor(enemyDamage(enemy) / 2);
            state.playerHp = Math.max(0, state.playerHp - counter);
            logBattle(`${enemy.name} контратакует на ${counter} урона.`);
        }

        if (enemy.hp <= 0) {
            handleWin();
            return;
        }

        handleEnemyTurn();
    }

    function restPlayer() {
        if (state.rests <= 0) {
            statusMessage.textContent = 'Запасы отдыха исчерпаны.';
            return;
        }
        const derived = derivePlayerStats();
        state.rests -= 1;
        state.playerHp = derived.maxHp;
        logBattle('Вы полностью восстановились.');
        updateBattleStatus();
    }

    function derivePlayerStats() {
        const totals = calcTotals();
        const level = state.level;
        const maxHp = 20 + (totals.constitution || 0) * 3 + level * 10;
        const luckBonus = Math.floor((totals.luck || 0) / 6);
        const dexBonus = Math.floor((totals.dexterity || 0) / 4);
        const classAttr = state.classId === 'leader' ? totals.charisma : state.classId === 'nerd' ? totals.intelligence : totals.dexterity;
        const attackBonus = Math.floor((classAttr || 0) / 4) + luckBonus + level;
        const baseDamageBonus = Math.floor((classAttr || 0) / 3);
        const ac = 10 + dexBonus + level;
        const critChance = 0.05 + (totals.luck || 0) / 20;
        return { totals, level, maxHp, luckBonus, attackBonus, baseDamageBonus, ac, classAttr: classAttr || 0, critChance };
    }

    function ensurePlayerReady() {
        if (!classSelect.value) {
            statusMessage.textContent = 'Выберите класс перед боем.';
            return false;
        }
        const unrolled = statKeys.filter(({ key }) => state.stats[key].base === null);
        if (unrolled.length) {
            statusMessage.textContent = 'Сначала бросьте все характеристики.';
            return false;
        }
        return true;
    }

    function animateButton(btn) {
        if (!btn) return;
        btn.classList.add('rolling');
        setTimeout(() => btn.classList.remove('rolling'), 520);
    }

    function flashCard(statKey) {
        const card = statsGrid?.querySelector(`[data-stat="${statKey}"]`);
        if (!card) return;
        card.classList.add('updated');
        setTimeout(() => card.classList.remove('updated'), 900);
    }

    function flashMetric(node) {
        const metric = node?.closest('.metric');
        if (!metric) return;
        metric.classList.add('updated');
        setTimeout(() => metric.classList.remove('updated'), 900);
    }

    function setTheme(mode) {
        const body = document.body;
        if (mode === 'dark') {
            body.classList.add('dark');
        } else {
            body.classList.remove('dark');
        }
        localStorage.setItem(THEME_KEY, mode);
        if (themeToggle) themeToggle.textContent = mode === 'dark' ? 'Светлая тема' : 'Темная тема';
    }

    function updateBattleStatus() {
        const derived = derivePlayerStats();
        const lab = labs[state.battleIndex] || null;
        if (state.playerMax === null) state.playerMax = derived.maxHp;
        if (state.playerHp === null) state.playerHp = derived.maxHp;
        const restText = `Отдыхов: ${state.rests}`;
        if (battleStatus) {
            battleStatus.textContent = `Уровень ${state.level} • HP ${state.playerHp}/${derived.maxHp} • AC ${derived.ac} • XP ${state.xp} • ${restText}`;
        }
        if (enemyStatus) {
            if (!state.enemy && lab) {
                enemyStatus.textContent = `Следующая цель: ${lab.name}`;
            } else if (state.enemy) {
                enemyStatus.textContent = `${state.enemy.name}: ${state.enemy.hp} HP • Защита ${state.enemy.defense}`;
            } else {
                enemyStatus.textContent = 'Все лабоработы побеждены!';
            }
        }
        if (battleRestBtn) battleRestBtn.textContent = `Отдохнуть (${state.rests})`;
        if (battleAttackBtn) battleAttackBtn.disabled = !state.enemy;
    }

    function logBattle(text) {
        if (!battleLog) return;
        const line = document.createElement('div');
        line.textContent = text;
        battleLog.appendChild(line);
        battleLog.scrollTop = battleLog.scrollHeight;
    }

    function startBattle() {
        if (!ensurePlayerReady()) return;
        if (state.battleIndex >= labs.length) {
            statusMessage.textContent = 'Все лабы уже пройдены!';
            return;
        }
        const derived = derivePlayerStats();
        state.playerMax = derived.maxHp;
        if (state.playerHp === null || state.playerHp > derived.maxHp) state.playerHp = derived.maxHp;
        const lab = labs[state.battleIndex];
        state.enemy = {
            id: lab.id,
            name: lab.name,
            hp: lab.hp(state.level),
            defense: lab.def(state.level),
            reward: lab.reward,
            special: lab.special,
            turn: 1,
        };
        state.enemyTurn = 1;
        state.aiPenalty = 0;
        if (battleLog) battleLog.innerHTML = '';
        if (battleAttackBtn) battleAttackBtn.disabled = false;
        logBattle(`Начинается бой: ${lab.name}`);
        updateBattleStatus();
    }

    function validateForm() {
        const missingFields = [];
        if (!nameInput.value.trim()) missingFields.push('Имя персонажа');
        if (!backgroundInput.value.trim()) missingFields.push('Предыстория');
        if (!classSelect.value) missingFields.push('Класс');
        if (!groupInput.value.trim()) missingFields.push('Номер группы');
        const unrolled = statKeys.filter(({ key }) => state.stats[key].base === null).map(s => s.label);

        if (missingFields.length || unrolled.length) {
            const messages = [];
            if (missingFields.length) messages.push(`Заполните: ${missingFields.join(', ')}`);
            if (unrolled.length) messages.push(`Бросьте характеристики: ${unrolled.join(', ')}`);
            validationMessage.textContent = messages.join(' • ');
            return false;
        }
        validationMessage.textContent = '';
        return true;
    }

    function saveCharacter() {
        if (!validateForm()) return;
        const payload = {
            name: nameInput.value.trim(),
            background: backgroundInput.value.trim(),
            classId: classSelect.value,
            group: groupInput.value.trim(),
            level: state.level,
            stats: state.stats,
        };
        localStorage.setItem(STORAGE_KEY, JSON.stringify(payload));
        statusMessage.textContent = 'Персонаж сохранён в листе (LocalStorage).';
    }

    function loadCharacter() {
        const raw = localStorage.getItem(STORAGE_KEY);
        if (!raw) return;
        try {
            const data = JSON.parse(raw);
            nameInput.value = data.name || '';
            backgroundInput.value = data.background || '';
            classSelect.value = data.classId || '';
            groupInput.value = data.group || '';
            state.level = data.level || 1;
            state.classId = data.classId || '';
            levelValue.textContent = state.level;
            if (data.stats) {
                state.stats = Object.fromEntries(statKeys.map(({ key }) => [
                    key,
                    { base: data.stats[key]?.base ?? null, total: data.stats[key]?.total ?? null },
                ]));
            }
            updateClassCallout();
            renderStats();
            statusMessage.textContent = 'Загружен сохранённый персонаж.';
        } catch {
            statusMessage.textContent = 'Не удалось прочитать сохранённый лист.';
        }
    }

    function resetCharacter() {
        nameInput.value = '';
        backgroundInput.value = '';
        classSelect.value = '';
        groupInput.value = '';
        state.classId = '';
        state.stats = Object.fromEntries(statKeys.map(({ key }) => [key, { base: null, total: null }]));
        state.freeRoll = null;
        if (freeRollValue) freeRollValue.textContent = '—';
        updateClassCallout();
        renderStats();
        validationMessage.textContent = '';
        statusMessage.textContent = 'Создан пустой лист. Все поля обязательны перед сохранением.';
    }

    function handleClassChange() {
        state.classId = classSelect.value;
        updateClassCallout();
        renderStats();
    }

    function bindEvents() {
        statsGrid.querySelectorAll('.mini-roll').forEach(btn => {
            btn.addEventListener('click', () => {
                const statKey = btn.dataset.roll;
                if (statKey) {
                    animateButton(btn);
                    rollStat(statKey);
                }
            });
        });
        rollAllBtn?.addEventListener('click', rollAllStats);
        saveBtn?.addEventListener('click', saveCharacter);
        newBtn?.addEventListener('click', resetCharacter);
        classSelect?.addEventListener('change', handleClassChange);
        [nameInput, backgroundInput, classSelect, groupInput].forEach(field => {
            field?.addEventListener('input', validateForm);
        });
        freeRollBtn?.addEventListener('click', rollFree);
        assignBtn?.addEventListener('click', assignFreeRoll);
        rollDamageIntBtn?.addEventListener('click', () => rollDamage('int'));
        rollDamageChaBtn?.addEventListener('click', () => rollDamage('cha'));
        themeToggle?.addEventListener('click', () => {
            const next = document.body.classList.contains('dark') ? 'light' : 'dark';
            setTheme(next);
        });
        battleStartBtn?.addEventListener('click', startBattle);
        battleAttackBtn?.addEventListener('click', playerAttack);
        battleRestBtn?.addEventListener('click', restPlayer);
    }

    bindEvents();
    updateClassCallout();
    renderStats();
    setTheme(localStorage.getItem(THEME_KEY) || 'light');
    loadCharacter();
    updateBattleStatus();
})();
