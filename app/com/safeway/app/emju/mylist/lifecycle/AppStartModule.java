package com.safeway.app.emju.mylist.lifecycle;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.safeway.app.emju.allocation.requestidentification.parser.ConsumerRequestParser;
import com.safeway.app.emju.allocation.requestidentification.parser.RequestParser;
import com.safeway.app.emju.mail.service.EmailDispatcherImp;
import com.safeway.app.emju.mylist.service.ItemDetailAsyncRetriever;
import com.safeway.app.emju.mylist.service.ItemDetailsProvider;
import com.safeway.app.emju.mylist.service.detail.CCItemDetailAsyncRetriever;
import com.safeway.app.emju.mylist.service.detail.PDItemDetailAsyncRetriever;
import com.safeway.app.emju.mylist.service.detail.UPCItemDetailAsyncRetriever;
import com.safeway.app.emju.mylist.service.detail.WSItemDetailAsyncRetriever;
import com.safeway.app.emju.mylist.service.detail.YCSItemDetailAsyncRetriever;
import com.safeway.app.emju.mylist.service.item.CCDetailsProvider;
import com.safeway.app.emju.mylist.service.item.ELPDetailsProvider;
import com.safeway.app.emju.mylist.service.item.FFDetailsProvider;
import com.safeway.app.emju.mylist.service.item.MCSDetailsProvider;
import com.safeway.app.emju.mylist.service.item.PDDetailsProvider;
import com.safeway.app.emju.mylist.service.item.RECDetailsProvider;
import com.safeway.app.emju.mylist.service.item.UPCDetailsProvider;
import com.safeway.app.emju.mylist.service.item.WSDetailsProvider;
import com.safeway.app.emju.mylist.service.item.YCSDetailsProvider;

public class AppStartModule extends AbstractModule {

	@Override
	protected void configure() {
		
		bind(RequestParser.class).to(ConsumerRequestParser.class);
		
		bind(ItemDetailsProvider.class).annotatedWith(Names.named("UPC")).to(UPCDetailsProvider.class);
		bind(ItemDetailsProvider.class).annotatedWith(Names.named("CC")).to(CCDetailsProvider.class);
		bind(ItemDetailsProvider.class).annotatedWith(Names.named("PD")).to(PDDetailsProvider.class);
		bind(ItemDetailsProvider.class).annotatedWith(Names.named("YCS")).to(YCSDetailsProvider.class);
		bind(ItemDetailsProvider.class).annotatedWith(Names.named("MCS")).to(MCSDetailsProvider.class);
		bind(ItemDetailsProvider.class).annotatedWith(Names.named("FF")).to(FFDetailsProvider.class);
		bind(ItemDetailsProvider.class).annotatedWith(Names.named("WS")).to(WSDetailsProvider.class);
		bind(ItemDetailsProvider.class).annotatedWith(Names.named("ELP")).to(ELPDetailsProvider.class);
		bind(ItemDetailsProvider.class).annotatedWith(Names.named("REC")).to(RECDetailsProvider.class);
		
		bind(ItemDetailAsyncRetriever.class).annotatedWith(Names.named("UPC")).to(UPCItemDetailAsyncRetriever.class);
		bind(ItemDetailAsyncRetriever.class).annotatedWith(Names.named("CC")).to(CCItemDetailAsyncRetriever.class);
		bind(ItemDetailAsyncRetriever.class).annotatedWith(Names.named("PD")).to(PDItemDetailAsyncRetriever.class);
		bind(ItemDetailAsyncRetriever.class).annotatedWith(Names.named("YCS")).to(YCSItemDetailAsyncRetriever.class);
		bind(ItemDetailAsyncRetriever.class).annotatedWith(Names.named("WS")).to(WSItemDetailAsyncRetriever.class);
		
        bind(EmailDispatcherImp.class).asEagerSingleton();
        
        bind(RequestParser.class).annotatedWith(Names.named("CRP")).to(ConsumerRequestParser.class);
	}

}
