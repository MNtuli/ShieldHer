-- Seed support resources. Safe to re-run: title is unique, existing rows are skipped.
-- VERIFY every number and link before the demo.
INSERT INTO support_resources (title, description, category, phone, url) VALUES
('SAPS Emergency', 'Police emergency line. Call this if you are in immediate danger.', 'HELPLINE', '10111', NULL),
('GBV Command Centre', 'Free, 24-hour helpline for gender-based violence. Counselling and referrals. You can also SMS HELP to 31531.', 'HELPLINE', '0800 428 428', 'https://www.gbv.org.za'),
('Stop Gender Violence Helpline', 'Free helpline offering support and referrals for survivors of abuse.', 'HELPLINE', '0800 150 150', NULL),
('SADAG Mental Health Line', 'Support for anxiety, depression and distress from the South African Depression and Anxiety Group.', 'HELPLINE', '0800 567 567', 'https://www.sadag.org'),
('LifeLine South Africa', 'Free counselling when you feel overwhelmed or alone.', 'HELPLINE', '0861 322 322', NULL),
('Childline South Africa', 'Free helpline for children and young people who are being harmed or threatened.', 'HELPLINE', '0800 055 555', 'https://www.childline.org.za'),
('Report online harassment to the police', 'Go to any police station and open a case. Take screenshots with dates and usernames. Online threats, stalking and sharing private images can be criminal offences.', 'REPORTING', NULL, NULL),
('Apply for a protection order', 'The Protection from Harassment Act lets you ask a magistrate''s court for an order against someone harassing you, including online. It is free to apply. The clerk of the court can help.', 'REPORTING', NULL, 'https://www.justice.gov.za'),
('Report a privacy breach', 'If your personal information or private images were shared without consent, you can complain to the Information Regulator.', 'REPORTING', NULL, 'https://inforegulator.org.za'),
('Report the account to the platform', 'Use the Report option on the post or profile (Instagram, Facebook, X, TikTok, WhatsApp). Block the account after you have saved evidence.', 'REPORTING', NULL, NULL),
('Save evidence first', 'Take screenshots showing the message, username and date. Save links. Do not delete anything, even if it is painful to look at. Store copies somewhere safe.', 'SAFETY_TIP', NULL, NULL),
('Lock down your accounts', 'Change passwords, turn on two-step verification, make profiles private and check which apps and devices are logged in.', 'SAFETY_TIP', NULL, NULL),
('Tell someone you trust', 'You do not have to carry this alone. Tell a friend, family member or counsellor, and add them as a trusted contact in ShieldHer.', 'SAFETY_TIP', NULL, NULL)
ON CONFLICT (title) DO NOTHING;
