INSERT INTO public.users (id, name, email, image_url, description, sub) VALUES (61096188690038784, 'NTNOM', null, 'LFKUYAVB', 'KUBHGWN', null);
INSERT INTO public.users (id, name, email, image_url, description, sub) VALUES (61096191353421824, 'GSKRASWL', null, 'WFIRYB', 'UENQNYLLRJ', null);
INSERT INTO public.users (id, name, email, image_url, description, sub) VALUES (61096191496028160, 'BHA', null, 'OAOOG', 'ZUWOGDXNVP', null);
INSERT INTO public.users (id, name, email, image_url, description, sub) VALUES (61096191542165504, 'HLBL', null, 'VQRTYKDCHP', 'PVBG', null);
INSERT INTO public.users (id, name, email, image_url, description, sub) VALUES (61096191621857280, 'HOSK', null, 'FXWMLHNBYD', 'FKK', null);
INSERT INTO public.users (id, name, email, image_url, description, sub) VALUES (61096191676383232, 'PZF', null, 'DWVK', 'MZUEP', null);
INSERT INTO public.users (id, name, email, image_url, description, sub) VALUES (61096191730909184, 'QUWYIE', null, 'NWAPKAJAAM', 'ITJCUEAK', null);
INSERT INTO public.users (id, name, email, image_url, description, sub) VALUES (61096191793823744, 'OEVBRYICI', null, 'FNEOJAZCHL', 'NSKLMTO', null);
INSERT INTO public.users (id, name, email, image_url, description, sub) VALUES (61096191848349696, 'NUW', null, 'CSQKNE', 'XFPX', null);
INSERT INTO public.users (id, name, email, image_url, description, sub) VALUES (61096191886098432, 'DNI', null, 'VDW', 'IGCRMZX', null);
INSERT INTO public.users (id, name, email, image_url, description, sub) VALUES (61096191928041472, 'KVOLBGRMO', null, 'QSF', 'SXDYP', null);

INSERT INTO public.communities (id, name, owner_id, image_url, base_permissions) VALUES (61096191995150336, 'KABENNFRE', 61096188690038784, 'LULHN', 224);

INSERT INTO public.community_members (community_id, user_id) VALUES (61096191995150336, 61096191353421824);
INSERT INTO public.community_members (community_id, user_id) VALUES (61096191995150336, 61096191496028160);
INSERT INTO public.community_members (community_id, user_id) VALUES (61096191995150336, 61096191542165504);
INSERT INTO public.community_members (community_id, user_id) VALUES (61096191995150336, 61096191621857280);
INSERT INTO public.community_members (community_id, user_id) VALUES (61096191995150336, 61096191676383232);
INSERT INTO public.community_members (community_id, user_id) VALUES (61096191995150336, 61096191730909184);
INSERT INTO public.community_members (community_id, user_id) VALUES (61096191995150336, 61096191793823744);
INSERT INTO public.community_members (community_id, user_id) VALUES (61096191995150336, 61096191848349696);
INSERT INTO public.community_members (community_id, user_id) VALUES (61096191995150336, 61096191886098432);
INSERT INTO public.community_members (community_id, user_id) VALUES (61096191995150336, 61096191928041472);
INSERT INTO public.community_members (community_id, user_id) VALUES (61096191995150336, 61096188690038784);

INSERT INTO public.channels (id, name, type, community_id) VALUES (61096193865809920, 'UIYM', 0, 61096191995150336);
INSERT INTO public.channels (id, name, type, community_id) VALUES (61096194029387776, 'FOOGTXMFRE', 0, 61096191995150336);
INSERT INTO public.channels (id, name, type, community_id) VALUES (61096194151022592, 'UTH', 0, 61096191995150336);
INSERT INTO public.channels (id, name, type, community_id) VALUES (61096194264268800, 'ILPIQ', 0, 61096191995150336);
INSERT INTO public.channels (id, name, type, community_id) VALUES (61096194352349184, 'RNFMAM', 0, 61096191995150336);

INSERT INTO public.roles (id, name, permission, community_id) VALUES (61096192741736448, 'UXCOVYG', 0, 61096191995150336);
INSERT INTO public.roles (id, name, permission, community_id) VALUES (61096192821428224, 'YPNBOPXYN', 0, 61096191995150336);
INSERT INTO public.roles (id, name, permission, community_id) VALUES (61096192880148480, 'YNLT', 0, 61096191995150336);
INSERT INTO public.roles (id, name, permission, community_id) VALUES (61096192913702912, 'HSU', 0, 61096191995150336);
INSERT INTO public.roles (id, name, permission, community_id) VALUES (61096192980811776, 'VJYZ', 0, 61096191995150336);

INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192913702912, 61096191353421824);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192980811776, 61096191353421824);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192880148480, 61096191496028160);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192980811776, 61096191496028160);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192980811776, 61096191542165504);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192880148480, 61096191542165504);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192913702912, 61096191621857280);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192980811776, 61096191621857280);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192913702912, 61096191676383232);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192980811776, 61096191676383232);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192913702912, 61096191730909184);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192980811776, 61096191730909184);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192880148480, 61096191793823744);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192741736448, 61096191793823744);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192913702912, 61096191848349696);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192821428224, 61096191848349696);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192880148480, 61096191886098432);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192980811776, 61096191886098432);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192821428224, 61096191928041472);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192913702912, 61096191928041472);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192913702912, 61096188690038784);
INSERT INTO public.user_roles (role_id, user_id) VALUES (61096192880148480, 61096188690038784);


INSERT INTO public.channel_roles (channel_id, role_id, permission) VALUES (61096193865809920, 61096192980811776, 136);
INSERT INTO public.channel_roles (channel_id, role_id, permission) VALUES (61096193865809920, 61096192821428224, 136);
INSERT INTO public.channel_roles (channel_id, role_id, permission) VALUES (61096194029387776, 61096192913702912, 136);
INSERT INTO public.channel_roles (channel_id, role_id, permission) VALUES (61096194029387776, 61096192880148480, 136);
INSERT INTO public.channel_roles (channel_id, role_id, permission) VALUES (61096194151022592, 61096192980811776, 136);
INSERT INTO public.channel_roles (channel_id, role_id, permission) VALUES (61096194151022592, 61096192880148480, 136);
INSERT INTO public.channel_roles (channel_id, role_id, permission) VALUES (61096194264268800, 61096192880148480, 136);
INSERT INTO public.channel_roles (channel_id, role_id, permission) VALUES (61096194264268800, 61096192913702912, 136);
INSERT INTO public.channel_roles (channel_id, role_id, permission) VALUES (61096194352349184, 61096192821428224, 136);
INSERT INTO public.channel_roles (channel_id, role_id, permission) VALUES (61096194352349184, 61096192741736448, 136);

INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('KVFFFF', null, 61096191496028160, 61096194432040960, 61096193865809920, null, 'CTINUEBM');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('PPQKP', null, 61096191848349696, 61096194486566912, 61096193865809920, null, 'RLRCM');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('ICSSDVK', null, 61096188690038784, 61096194511732736, 61096193865809920, null, 'BSURWNQBX');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('CJCSBHDZ', null, 61096191496028160, 61096194536898560, 61096193865809920, null, 'IGZYQWP');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('TXBUMYUQM', null, 61096191730909184, 61096194566258688, 61096193865809920, null, 'GOIHYZK');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('AURNRCKAPG', null, 61096191353421824, 61096194587230208, 61096193865809920, null, 'TFJKBLCC');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('XOX', null, 61096191730909184, 61096194616590336, 61096193865809920, null, 'UPOQZM');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('GXWBIZ', null, 61096191542165504, 61096194641756160, 61096193865809920, null, 'FYJ');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('HYLXBLSP', null, 61096188690038784, 61096194662727680, 61096193865809920, null, 'JUFUESQBJQ');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('OAUVPBHINU', null, 61096191621857280, 61096194692087808, 61096193865809920, null, 'RYCIP');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('FAXYJD', null, 61096191848349696, 61096194717253632, 61096194029387776, null, 'AAHIGXF');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('BNUCMEGAPZ', null, 61096191676383232, 61096194759196672, 61096194029387776, null, 'OAHJS');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('IPVIUSLKO', null, 61096191496028160, 61096194784362496, 61096194029387776, null, 'CAJ');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('GROYMQDJXC', null, 61096191886098432, 61096194817916928, 61096194029387776, null, 'ORVI');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('DRUFLOHBF', null, 61096191621857280, 61096194847277056, 61096194029387776, null, 'PQUP');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('ODVOYGHN', null, 61096191730909184, 61096194872442880, 61096194029387776, null, 'QLQTDMD');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('UTWUMBXUBB', null, 61096191542165504, 61096194926968832, 61096194029387776, null, 'ZGCPT');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('OXFRUX', null, 61096188690038784, 61096194960523264, 61096194029387776, null, 'MMK');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('XQCS', null, 61096191928041472, 61096194985689088, 61096194029387776, null, 'NIJEQHB');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('KRLBDX', null, 61096191542165504, 61096195010854912, 61096194029387776, null, 'YHFD');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('SLRS', null, 61096191730909184, 61096195048603648, 61096194151022592, null, 'NVFUNJO');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('LFNFDFM', null, 61096191496028160, 61096195069575168, 61096194151022592, null, 'UYKEIOF');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('ZSF', null, 61096191928041472, 61096195103129600, 61096194151022592, null, 'GAQC');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('TWZ', null, 61096191621857280, 61096195128295424, 61096194151022592, null, 'ZFPURGLHHN');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('AGGPC', null, 61096191353421824, 61096195153461248, 61096194151022592, null, 'CHYX');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('BVA', null, 61096191676383232, 61096195182821376, 61096194151022592, null, 'BJUZ');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('ZTLG', null, 61096191793823744, 61096195203792896, 61096194151022592, null, 'AXD');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('GNNAMEYHH', null, 61096191793823744, 61096195233153024, 61096194151022592, null, 'PBIRW');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('SJTEY', null, 61096191621857280, 61096195262513152, 61096194151022592, null, 'WENLBMFPRW');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('VZLMMFX', null, 61096191621857280, 61096195279290368, 61096194151022592, null, 'TBOPR');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('MGJFCB', null, 61096191730909184, 61096195304456192, 61096194264268800, null, 'DPYNTCBIC');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('OXGDWSGXW', null, 61096191730909184, 61096195333816320, 61096194264268800, null, 'PQUWET');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('LHEKKNIM', null, 61096191928041472, 61096195354787840, 61096194264268800, null, 'ROTG');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('GDMVZA', null, 61096191542165504, 61096195384147968, 61096194264268800, null, 'DMVN');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('TKEEGBQPP', null, 61096191886098432, 61096195409313792, 61096194264268800, null, 'BDST');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('ANHOHOZP', null, 61096188690038784, 61096195430285312, 61096194264268800, null, 'EXDI');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('QYLFELWRV', null, 61096191542165504, 61096195463839744, 61096194264268800, null, 'MGRVCD');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('SGGTYTEJTA', null, 61096191542165504, 61096195489005568, 61096194264268800, null, 'QNNYNNEO');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('QADHPLWOWC', null, 61096188690038784, 61096195514171392, 61096194264268800, null, 'MTRO');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('FIGYEWCIGZ', null, 61096191928041472, 61096195543531520, 61096194264268800, null, 'NMVD');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('LCIS', null, 61096191676383232, 61096195568697344, 61096194352349184, null, 'FOKRSHW');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('MBO', null, 61096191928041472, 61096195606446080, 61096194352349184, null, 'ZYRSFI');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('PUZHG', null, 61096188690038784, 61096195631611904, 61096194352349184, null, 'MGPVIEIU');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('GFHRKUVOD', null, 61096191353421824, 61096195673554944, 61096194352349184, null, 'WRU');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('AEDGEIZYW', null, 61096191496028160, 61096195702915072, 61096194352349184, null, 'HCO');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('IPDVFZXIV', null, 61096191793823744, 61096195732275200, 61096194352349184, null, 'QHHQQTQUG');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('KYFQI', null, 61096191676383232, 61096195757441024, 61096194352349184, null, 'ATYONWAFF');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('TPXH', null, 61096191730909184, 61096195778412544, 61096194352349184, null, 'NGOTX');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('JAHLCID', null, 61096191676383232, 61096195807772672, 61096194352349184, null, 'XBWZJN');
INSERT INTO public.messages (text, updated_at, user_id, message_id, channel_id, responds_to_message_id, gif_link) VALUES ('DSQWJ', null, 61096191353421824, 61096195837132800, 61096194352349184, null, 'QPANJMH');
