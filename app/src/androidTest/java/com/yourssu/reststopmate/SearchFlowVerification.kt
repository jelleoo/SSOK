package com.yourssu.reststopmate

import android.app.Activity
import android.app.Instrumentation
import android.app.UiAutomation
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.accessibility.AccessibilityNodeInfo

/** Real-device smoke check. Never clears guest state or existing search history. */
class SearchFlowVerification : Instrumentation() {
    private var phase = "select"
    private var homeScrollAnchor: String? = null

    override fun onCreate(arguments: Bundle?) {
        super.onCreate(arguments)
        phase = arguments?.getString("phase") ?: "select"
        start()
    }

    private val automation get() = getUiAutomation(UiAutomation.FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES)

    override fun onStart() {
        val result = Bundle()
        val status = Bundle().apply {
            putString("class", SearchFlowVerification::class.java.name)
            putString("test", phase)
            putInt("numtests", 1)
            putInt("current", 1)
        }
        sendStatus(1, status)
        try {
            val info = automation.serviceInfo
            info.flags = info.flags or android.accessibilityservice.AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            automation.serviceInfo = info
            val activity = startActivitySync(Intent(targetContext, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            android.os.ParcelFileDescriptor.AutoCloseInputStream(
                automation.executeShellCommand("am task focus ${activity.taskId}")
            ).use { it.readBytes() }
            waitForHome()
            if (phase == "integration") verifyHomeMenu()
            tap("Search")
            waitFor("최근 검색 항목")
            if (phase == "integration-restore") {
                waitFor("안성휴게소")
                tap("안성휴게소")
                waitFor("안성국밥")
                waitFor("10,000원")
                tap("닫기")
                waitFor("최근 검색 항목")
                tap("Home")
                waitForHome()
                result.putString("verification", "PASS: guest Home and recent history restored after process restart; saved item opens real featured menu")
            } else if (phase == "all-menus" || phase == "integration") {
                query("안성")
                waitFor("안성휴게소")
                tap("안성휴게소")
                waitFor("안성국밥")
                waitFor("10,000원")
                tap("전체 메뉴 보기")
                waitFor("전체 메뉴 28개")
                waitFor("한우소머리국밥")
                waitFor("13,000원")
                for (attempt in 0 until 12) {
                    if (hasText("속풀이생라면")) break
                    val grid = nodes().firstOrNull { it.isScrollable } ?: error("Menu grid not scrollable")
                    check(grid.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD))
                    SystemClock.sleep(450)
                }
                waitFor("속풀이생라면")
                waitFor("6,000원")
                tap("대표 메뉴로")
                waitFor("안성국밥")
                waitFor("10,000원")
                tap("닫기")
                waitFor("검색 결과")
                check(!hasText("망향휴게소")) { "Search filter was not preserved" }
                if (phase == "integration") {
                    tap("My")
                    waitFor("자주 방문한 휴게소")
                    waitFor("이번 달 휴게소 먹방 횟수")
                    waitFor("8회")
                    tap("Search")
                    waitFor("검색 결과")
                    waitFor("안성휴게소")
                    check(!hasText("망향휴게소")) { "Tab switch lost Search query" }
                    tap("지우기")
                    waitFor("최근 검색 항목")
                    waitFor("안성휴게소")
                    tap("Home")
                    waitForHome()
                    homeScrollAnchor?.let { anchor ->
                        check(visibleHomeStops().firstOrNull() == anchor) { "Home scroll position was lost" }
                    }
                }
                result.putString("verification", "PASS: featured to full menu, 28 real items with prices, scroll to last item, back to featured, preserved search")
            } else if (phase == "detail") {
                query("안성")
                waitFor("안성휴게소")
                tap("안성휴게소")
                waitFor("대표 메뉴")
                waitFor("안성국밥")
                waitFor("10,000원")
                tap("닫기")
                waitFor("검색 결과")
                tap("지우기")
                waitFor("최근 검색 항목")
                tap("안성휴게소")
                waitFor("안성국밥")
                waitFor("10,000원")
                automation.takeScreenshot()?.let { image ->
                    val file = java.io.File(targetContext.getExternalFilesDir(null), "featured-menu-verified.png")
                    java.io.FileOutputStream(file).use { image.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
                    image.recycle()
                }
                tap("닫기")
                waitFor("최근 검색 항목")
                result.putString("verification", "PASS: search result and recent item open Anseong featured menu with real 10,000 KRW price; close preserves search/history")
            } else if (phase == "restore") {
                waitFor("망향휴게소")
                waitFor("안성휴게소")
                val names = nodes().mapNotNull { it.text?.toString() }
                    .filter { it == "망향휴게소" || it == "안성휴게소" }
                check(names == listOf("망향휴게소", "안성휴게소")) { "History order or duplicates: $names" }
                tap("안성휴게소")
                waitFor("안성국밥")
                waitFor("10,000원")
                tap("닫기")
                waitFor("최근 검색 항목")
                result.putString("verification", "PASS: relaunch restores unique newest-first history; recent item opens featured menu")
            } else {
                query("없는휴게소XYZ")
                waitFor("검색 결과가 없어요.")
                query("안 성")
                waitFor("안성휴게소")
                check(!hasText("망향휴게소")) { "Search did not filter" }
                tap("안성휴게소")
                waitFor("안성국밥")
                waitFor("10,000원")
                tap("닫기")
                tap("지우기")
                waitFor("최근 검색 항목")
                waitFor("안성휴게소")
                // Repeat selection: the saved list must remain unique.
                query("안성")
                waitFor("안성휴게소")
                tap("안성휴게소")
                waitFor("대표 메뉴")
                tap("닫기")
                SystemClock.sleep(400)
                query("망향")
                waitFor("망향휴게소")
                tap("망향휴게소")
                waitFor("대표 메뉴")
                tap("닫기")
                SystemClock.sleep(400)
                tap("지우기")
                waitFor("안성휴게소")
                waitFor("망향휴게소")
                val names = nodes().mapNotNull { it.text?.toString() }
                    .filter { it == "망향휴게소" || it == "안성휴게소" }
                check(names == listOf("망향휴게소", "안성휴게소")) { "History order or duplicates: $names" }
                query("휴게소")
                waitFor("안성휴게소")
                check(nodes().any { it.isScrollable }) { "API results have no draggable overflow" }
                result.putString("verification", "PASS: Korean API filtering, empty result, selection persistence, deduplication, newest-first order, scrollable results")
            }
            sendStatus(0, status)
            finish(Activity.RESULT_OK, result)
        } catch (error: Throwable) {
            result.putString("verification", "FAIL: ${error.stackTraceToString()}")
            status.putString("stack", error.stackTraceToString())
            sendStatus(-2, status)
            finish(Activity.RESULT_CANCELED, result)
        }
    }

    private fun waitForHome() {
        val end = SystemClock.uptimeMillis() + 45_000
        while (SystemClock.uptimeMillis() < end) {
            val visible = nodes().mapNotNull { it.text?.toString() }
            if ("가까운 휴게소" in visible || "휴게소까지" in visible) return
            SystemClock.sleep(250)
        }
        error("Home did not load: ${nodes().mapNotNull { it.text }}")
    }

    private fun visibleHomeStops() = nodes().filter { it.isVisibleToUser }
        .mapNotNull { it.text?.toString() }
        .filter { it.endsWith("휴게소") && it != "가까운 휴게소" }

    private fun verifyHomeMenu() {
        waitFor("안성휴게소")
        tap("안성휴게소")
        waitFor("안성휴게소 메뉴")
        tap("높은 가격순")
        tap("낮은 가격순")
        waitFor("4,000원")
        val prices = nodes().mapNotNull { it.text?.toString() }
            .filter { it.matches(Regex("[0-9,]+원")) }
            .map { it.replace(",", "").removeSuffix("원").toInt() }
        check(prices.size >= 2 && prices.zipWithNext().all { (a, b) -> a <= b })
        var back = nodes().first { it.contentDescription?.toString() == "뒤로가기" }
        while (!back.isClickable) back = back.parent ?: error("Home menu back is not clickable")
        check(back.performAction(AccessibilityNodeInfo.ACTION_CLICK))
        waitForHome()
        nodes().firstOrNull { it.isScrollable }?.let { row ->
            check(row.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD))
            SystemClock.sleep(600)
            homeScrollAnchor = visibleHomeStops().firstOrNull()
        }
    }

    private fun nodes(): List<AccessibilityNodeInfo> {
        // Compose can replace a whole screen without invalidating UiAutomation's node cache.
        if (android.os.Build.VERSION.SDK_INT >= 33) automation.clearCache()
        val result = mutableListOf<AccessibilityNodeInfo>()
        fun walk(node: AccessibilityNodeInfo?) {
            if (node == null || node.packageName?.toString() != targetContext.packageName) return
            result.add(node)
            for (i in 0 until node.childCount) walk(node.getChild(i))
        }
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            val windows = automation.windowsOnAllDisplays
            for (index in 0 until windows.size()) {
                windows.valueAt(index).forEach { walk(it.root) }
            }
        } else {
            automation.windows.forEach { walk(it.root) }
        }
        return result.filter { it.isVisibleToUser && it.packageName?.toString() == targetContext.packageName }
    }

    private fun hasText(text: String) = nodes().any { it.text?.toString() == text }

    private fun waitFor(text: String) {
        val end = SystemClock.uptimeMillis() + 30_000
        while (SystemClock.uptimeMillis() < end) {
            if (hasText(text)) return
            SystemClock.sleep(250)
        }
        error("Missing text: $text; visible: ${nodes().mapNotNull { it.text }}")
    }

    private fun tap(text: String) {
        waitFor(text)
        var node = nodes().firstOrNull { it.text?.toString() == text } ?: error("Cannot tap $text")
        while (!node.isClickable) node = node.parent ?: error("Not clickable: $text")
        check(node.performAction(AccessibilityNodeInfo.ACTION_CLICK)) { "Tap failed: $text" }
        SystemClock.sleep(200)
    }

    private fun query(value: String) {
        val field = nodes().firstOrNull { it.isEditable || it.className?.toString() == "android.widget.EditText" } ?: error("Search input missing")
        check(field.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, value)
        })) { "Cannot enter Korean query" }
        SystemClock.sleep(300)
    }
}
