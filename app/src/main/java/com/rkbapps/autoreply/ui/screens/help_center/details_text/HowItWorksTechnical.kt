package com.rkbapps.autoreply.ui.screens.help_center.details_text

const val HOW_IT_WORKS_TECHNICAL = "<h2>The Technical Magic: How Auto Reply Works</h2>\n" +
        "\n" +
        "<p>Auto Reply acts like a smart assistant in your phone. Here's how it technically works when a new message arrives:</p>\n" +
        "\n" +
        "<p><b>1. Listening for Notifications:</b><br>\n" +
        "The app asks for Notification Access permission so it can listen for new message alerts from other apps.</p>\n" +
        "\n" +
        "<p><b>How:</b> It listens for Android system announcements and detects new messages by reading their notifications.</p>\n" +
        "\n" +
        "<p><b>2. Identifying the App:</b><br>\n" +
        "It checks which app sent the notification (e.g., WhatsApp, Instagram).</p>\n" +
        "\n" +
        "<p><b>How:</b> Every notification has a <code>packageName</code> (e.g., <code>com.whatsapp</code>). The app matches it with your configured list.</p>\n" +
        "\n" +
        "<p><b>3. Group vs Personal Messages:</b><br>\n" +
        "It tries to guess if the message is from a group or individual chat.</p>\n" +
        "\n" +
        "<p><b>How:</b> Based on clues in the notification title or summary (like group name or sender info), the app makes a best guess.</p>\n" +
        "\n" +
        "<p><b>4. Matching Rules:</b><br>\n" +
        "It checks your rules to decide which reply to send.</p>\n" +
        "\n" +
        "<p><b>How:</b> Rules could be like:\n" +
        "<ul>\n" +
        "<li>Reply \"In a meeting\" to your boss.</li>\n" +
        "<li>Reply \"I'll get back soon\" to messages with \"urgent\".</li>\n" +
        "<li>Reply differently to group vs individual messages.</li>\n" +
        "</ul>\n" +
        "</p>\n" +
        "\n" +
        "<p><b>5. Preparing the Reply:</b><br>\n" +
        "It fetches the correct reply message based on the rule.</p>\n" +
        "\n" +
        "<p><b>How:</b> It looks up your saved text and settings like reply delay or one-time-per-contact behavior.</p>\n" +
        "\n" +
        "<p><b>6. Sending the Message:</b><br>\n" +
        "It sends the reply using the original app's notification reply action.</p>\n" +
        "\n" +
        "<p><b>How:</b> It finds the \"Reply\" action in the notification and uses Android’s <code>RemoteInput</code> API to send the message as if you typed it yourself.</p>\n" +
        "\n" +
        "<p><b>7. Marking as Read & Cleanup:</b><br>\n" +
        "After replying, it may mark the message as read and dismiss the notification.</p>\n" +
        "\n" +
        "<p><b>Why:</b> This avoids duplicate replies if the same notification thread updates again.</p>\n" +
        "\n" +
        "<p>So in short, the Auto Reply app reads your notifications, matches rules, sends smart replies, and manages notifications — all without you touching your phone!</p>\n"